package client.ui.swing;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;
 
/**
 * Classe criada para definir formato de data ao utilizar 
 * a classe JDatePickerImpl. 
 * <b>Caso queira mudar o formato da data, basta alterar 
 * a variável datePattern</b>.
 * @author romuloponciano
 *
 */
public class DateLabelFormatter extends AbstractFormatter implements Serializable {
 
	private static final long serialVersionUID = 4951009520890590390L;
	
	private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
     
    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }
 
    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }
         
        return "";
    }
 
}
