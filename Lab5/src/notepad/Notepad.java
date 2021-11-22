//c00242666
//Dermot Berry

package notepad;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

@SuppressWarnings("serial")
public class Notepad extends JFrame {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	String dialogBox = JOptionPane/showInputDialog("Please enter your  password");
	

	private static final String UNTITLED = "Untitled";

	private final JFileChooser fileChooser = new JFileChooser();

	private final JTextArea textArea = new JTextArea();

	private boolean modified = false;

	public Notepad() {
		final JMenu fileMenu = new JMenu("File");

		fileMenu.add(new FileAction("New", this::newFile));
		fileMenu.add(new FileAction("Open", this::openFile));
		fileMenu.add("Save").addActionListener(event -> saveFile());
		fileMenu.addSeparator();
		fileMenu.add(new FileAction("Exit", () -> System.exit(0)));

		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				modified = true;

			}
		});
		add(new JScrollPane(textArea));

		setTitle(UNTITLED);
		setPreferredSize(new Dimension(600, 400));
		pack();
	}

	private void newFile() {
		setTitle(UNTITLED);
		textArea.setText(null);
		modified = false;
	}

	// Opening a file can be a time-consuming task. Therefore, we open it in a
	// worker thread.
	private class FileOpener extends SwingWorker<byte[], Void> {

		private final Path path;

		public FileOpener(final Path path) {
			this.path = path;
		}

		@Override
		protected byte[] doInBackground() throws IOException {
			return Files.readAllBytes(path);
		}

		@Override
		protected void done() {
			try {
				byte[] data = get();
				// TODO: Do we need to decrypt the data?
				
				textArea.setText(new String(data, StandardCharsets.UTF_8));
				setTitle(path.toString());
				modified = false;
			} catch (final InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	
	private void openFile() {
		
		final int choice = fileChooser.showOpenDialog(this);
		
		if (choice == JFileChooser.APPROVE_OPTION) {
			(new FileOpener(fileChooser.getSelectedFile().toPath())).execute();
		}
	}



	// Saving a file can be a time-consuming task. Therefore, we save it in a
	// worker thread.
	private class FileSaver extends SwingWorker<Void, Void> {

		private final Path path;
		private final String text;

		public FileSaver(final Path path, final String text) {
			this.path = path;
			this.text = text;
		}

		private byte[] encrypt(final Cipher cipher, final Key key,
				final byte[] initialisationVector, final byte[] data)
				throws InvalidKeyException, InvalidAlgorithmParameterException,
				IllegalBlockSizeException, BadPaddingException {
			cipher.init(Cipher.ENCRYPT_MODE, key,
					new IvParameterSpec(initialisationVector));
			return cipher.doFinal(data);
		}

		private byte[] decrypt(final Cipher cipher, final Key key,
				final byte[] initialisationVector, final byte[] data)
				throws InvalidKeyException, InvalidAlgorithmParameterException,
				IllegalBlockSizeException, BadPaddingException {
			cipher.init(Cipher.DECRYPT_MODE, key,
					new IvParameterSpec(initialisationVector));
			return cipher.doFinal(data);
		}



		@Override
		protected Void doInBackground() throws IOException,NoSuchAlgorithmException, NoSuchProviderException,
		NoSuchPaddingException, InvalidKeyException,
		InvalidAlgorithmParameterException, IllegalBlockSizeException,
		BadPaddingException {
	final String keytext = "thebestsecretkey";

	final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
	final Key key = new SecretKeySpec(keytext.getBytes(), "AES");

	SecureRandom random = new SecureRandom();
	byte[] iv = new byte[cipher.getBlockSize()];
	random.nextBytes(iv);

	System.out.println("Plaintext: " + text);

	final byte[] ciphertext = encrypt(cipher, key, iv,
			text.getBytes()); 										//changed plaintext to text to match
	System.out.println("Ciphertext: " + Hex.toHexString(ciphertext));

	final String plaintext2 = new String(
			decrypt(cipher, key, iv, ciphertext));
	System.out.println("Plaintext (decrypted): " + plaintext2);

	assert (text.equals(plaintext2));
	Files.write(path, ciphertext);
	return null;
}

		@Override
		protected void done() {
			setTitle(path.toAbsolutePath().toString());
			modified = false;
		}
	}

	private void saveFile() {
		Path path = null;
		if (getTitle().equals(UNTITLED)) {
			int choice = fileChooser.showSaveDialog(this);
			if (choice == JFileChooser.APPROVE_OPTION) {
				path = fileChooser.getSelectedFile().toPath();
			} else {
				return;
			}
		} else {
			path = Paths.get(getTitle());
		}
		(new FileSaver(path, textArea.getText())).execute();
	}

	private class FileAction extends AbstractAction {

		private Runnable action;

		public FileAction(String name, Runnable action) {
			super(name);
			this.action = action;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (modified) {
				int choice = JOptionPane.showConfirmDialog(Notepad.this,
						"The text in " + getTitle()
								+ " has changed\nDo you want to save it?",
						"Notepad", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);
				switch (choice) {
				case JOptionPane.YES_OPTION:
					saveFile();
					action.run();
					break;
				case JOptionPane.NO_OPTION:
					action.run();
				default:
					// cancel
				}
			} else {
				action.run();
			}

		}
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(() -> {
			final Notepad notepad = new Notepad();
			notepad.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			notepad.setVisible(true);
		});
	}
}
