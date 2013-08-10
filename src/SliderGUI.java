import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

public class SliderGUI extends JPanel implements ChangeListener,
		PropertyChangeListener {
	JFormattedTextField textField;
	JSlider slider;

	public SliderGUI(String weight, int sliderMin, int sliderMax,
			int majorSpacing, int minorSpacing, int defaultValue) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		NumberFormatter formatter = new NumberFormatter(
				NumberFormat.getNumberInstance(java.util.Locale.US));
		formatter.setAllowsInvalid(true);

		textField = new JFormattedTextField(formatter);

		slider = new JSlider(sliderMin, sliderMax, defaultValue);

		// Slider
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(majorSpacing);
		slider.setMinorTickSpacing(minorSpacing);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		Font font = new Font("Times", Font.PLAIN, 10);
		slider.setFont(font);

		// Textfield
		textField.setColumns(5);
		textField.setText("" + new Integer(slider.getValue()));
		textField.addPropertyChangeListener(this);
		add(textField);
		add(slider);
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(weight),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		// Make the textfield at proper position
		layout.putConstraint(SpringLayout.WEST, textField, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, textField, 5,
				SpringLayout.NORTH, this);
		// layout.putConstraint(SpringLayout.SOUTH, textField, 5,
		// SpringLayout.SOUTH, this);
		// Make slider at proper position
		layout.putConstraint(SpringLayout.WEST, slider, 5, SpringLayout.EAST,
				textField);
		layout.putConstraint(SpringLayout.NORTH, slider, 5, SpringLayout.NORTH,
				this);
		// Adjust constraints for actual pane
		layout.putConstraint(SpringLayout.EAST, this, 5, SpringLayout.EAST,
				slider);
		layout.putConstraint(SpringLayout.SOUTH, this, 5, SpringLayout.SOUTH,
				slider);
		setSize(400, 120);

	}

	public int randInt2(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	// Listen to the slider
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		int textValue = (int) source.getValue();
		textField.setText("" + textValue);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if ("value".equals(e.getPropertyName())) {
			Number value = (Number) e.getNewValue();
			try {
				slider.setValue((int) value.doubleValue());
			} catch (NullPointerException n) {
				System.out.println("Input an integer");
			}
		}
	}

}
