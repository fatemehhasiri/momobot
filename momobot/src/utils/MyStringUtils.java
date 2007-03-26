package utils;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;

/**
 * @author Administrator
 */
public abstract class MyStringUtils {
    /**
     * mon tableau avec le code morse.
     */
    private static Map < Character, String > codeMorse = new TreeMap < Character, String >();
    static {
        codeMorse.put(new Character('A'), ".-");
        codeMorse.put(new Character('B'), "-...");
        codeMorse.put(new Character('C'), "-.-.");
        codeMorse.put(new Character('D'), "-..");
        codeMorse.put(new Character('E'), ".");
        codeMorse.put(new Character('F'), "..-.");
        codeMorse.put(new Character('G'), "--.");
        codeMorse.put(new Character('H'), "....");
        codeMorse.put(new Character('I'), "..");
        codeMorse.put(new Character('J'), ".---");
        codeMorse.put(new Character('K'), "-.-");
        codeMorse.put(new Character('L'), ".-..");
        codeMorse.put(new Character('M'), "--");
        codeMorse.put(new Character('N'), "-.");
        codeMorse.put(new Character('O'), "---");
        codeMorse.put(new Character('P'), ".--.");
        codeMorse.put(new Character('Q'), "--.-");
        codeMorse.put(new Character('R'), ".-.");
        codeMorse.put(new Character('S'), "...");
        codeMorse.put(new Character('T'), "-");
        codeMorse.put(new Character('U'), "..-");
        codeMorse.put(new Character('V'), "...-");
        codeMorse.put(new Character('W'), ".--");
        codeMorse.put(new Character('X'), "-..-");
        codeMorse.put(new Character('Y'), "-.--");
        codeMorse.put(new Character('Z'), "--..");
        codeMorse.put(new Character('1'), ".----");
        codeMorse.put(new Character('2'), "..---");
        codeMorse.put(new Character('3'), "...--");
        codeMorse.put(new Character('4'), "....-");
        codeMorse.put(new Character('5'), ".....");
        codeMorse.put(new Character('6'), "-....");
        codeMorse.put(new Character('7'), "--...");
        codeMorse.put(new Character('8'), "---..");
        codeMorse.put(new Character('9'), "----.");
        codeMorse.put(new Character('0'), "-----");
        codeMorse.put(new Character('.'), ".-.-.-");
        codeMorse.put(new Character(','), "--..--");
        codeMorse.put(new Character(':'), "---...");
        codeMorse.put(new Character('?'), "..--..");
        codeMorse.put(new Character('\''), ".----.");
        codeMorse.put(new Character('-'), "-....-");
        codeMorse.put(new Character('/'), "-..-.");
        codeMorse.put(new Character('('), "-.--.-");
        codeMorse.put(new Character(')'), "-.--.-");
        codeMorse.put(new Character('"'), ".-..-.");
        codeMorse.put(new Character('@'), ".--.-.");
        codeMorse.put(new Character('='), "-...-");
        codeMorse.put(new Character(' '), " ");
    }

    /**
     * @param work
     *            la string � d�pouiller
     * @return la string sans les accents
     */
    public static String effaceAccents(final String work) {
        return work.replace('�', 'o').replace('�', 'i').replace('�', 'i')
                .replace('�', 'e').replace('�', 'e').replace('�', 'e').replace(
                        '�', 'e').replace('�', 'a').replace('�', 'a').replace(
                        '�', 'a');
    }

    /**
     * m�thode pour le wquizz.
     * @param work
     *            ce que je dois nettoyer
     * @return un string propre.
     */
    public static String nettoieReponse(final String work) {
        String temp = effaceAccents(work);
        temp = StringUtils.replaceChars(temp, '-', ' ');
        temp = StringUtils.replaceChars(temp, '\'', ' ');
        temp = StringUtils.replaceChars(temp, '^', ' ');
        temp = StringUtils.replaceChars(temp, '�', ' ');
        temp = StringUtils.trim(temp);
        temp = StringUtils.removeStart(temp, "l ");
        temp = StringUtils.removeStart(temp, "la ");
        temp = StringUtils.removeStart(temp, "le ");
        temp = StringUtils.removeStart(temp, "les ");
        temp = StringUtils.removeStart(temp, "un ");
        temp = StringUtils.removeStart(temp, "une ");
        temp = StringUtils.removeStart(temp, "des ");
        temp = StringUtils.removeStart(temp, "du ");
        temp = StringUtils.removeStart(temp, "d ");
        temp = StringUtils.removeStart(temp, "a ");
        temp = StringUtils.removeStart(temp, "au ");
        temp = StringUtils.removeStart(temp, "aux ");
        temp = StringUtils.removeStart(temp, "en ");
        temp = StringUtils.removeStart(temp, "vers ");
        temp = StringUtils.removeStart(temp, "chez ");
        temp = StringUtils.removeStart(temp, "dans ");
        return StringUtils.removeEnd(temp, "?");
    }

    /**
     * @param seq
     *            une chaine � shaker
     * @return la chaine randomisee
     */
    public static String shuffle(final String seq) {
        final Random r = new Random();
        final StringBuffer input = new StringBuffer(seq);
        final StringBuffer output = new StringBuffer();
        for (int len = 0; len < seq.length(); len++) {
            final int k = r.nextInt(len);
            output.append(input.charAt(k));
            input.deleteCharAt(k);
        }
        return output.toString();
    }

    /**
     * @param work
     *            la cha�ne � convertir en Morse
     * @return la chaine en Morse
     */
    public static StringBuffer toMorse(final String work) {
        final StringBuffer output = new StringBuffer();
        for (final char element : work.toUpperCase().toCharArray()) {
            output.append(codeMorse.get(new Character(element)));
        }
        return output;
    }

    /**
     * constructeur cach�.
     */
    protected MyStringUtils() {
        throw new UnsupportedOperationException();
    }
}
