import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class OpenFile extends JPanel implements ActionListener {
	JButton open;
	JFileChooser fileChooser;
	BufferedImage image;

	public OpenFile() {
		open = new JButton("Open");
		open.addActionListener(this);
		add(open);

	}

	public void getImage(File img) {
		try {
			image = ImageIO.read(img);
		} catch (IOException e) {
			System.out.println("IOEXCEPTION ENCOUNTERED!");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == open) {
			fileChooser = new JFileChooser();
			PictureFilter filter = new PictureFilter();
			filter.add("jpg");
			filter.add("gif");
			filter.add("png");
			filter.setDescription("Images");
			fileChooser.setFileFilter(filter);
			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File img = fileChooser.getSelectedFile();
				getImage(img);
			} else {
				System.out.println("Cancelled by user");
			}
		}

	}

	class PictureFilter extends FileFilter {

		ArrayList<String> list = new ArrayList<String>();
		private String description;

		public void add(String type) {
			list.add(type);
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			} else if (f.isFile()) {
				Iterator iterator = list.iterator();
				while (iterator.hasNext()) {
					if (f.getName().endsWith((String) iterator.next())) {
						return true;
					}

				}
			}
			return false;

		}

		@Override
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}
}
