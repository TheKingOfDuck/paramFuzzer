import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;

public class test {

//    public static StringBuffer readFileContent(String fileName) {
//        File file = new File(fileName);
//        BufferedReader reader = null;
//        String[] sbf = {};
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String tempStr;
//            while ((tempStr = reader.readLine()) != null) {
//                sbf.;
//            }
//            reader.close();
//            return sbf;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//        return sbf;
//    }
public static void main(String[] args) {
    Options options = new Options();
    Option option0 = OptionBuilder.hasArg(true).create("t");
    option0.setArgName("t");
    Option option1 = OptionBuilder.hasArg(true).create("c");
    option1.setArgName("c");
    options.addOption(option0);
    options.addOption(option1);

    PosixParser parser = new PosixParser();
    try {
        CommandLine commandLine = parser.parse(options, args);
        for (Option option : commandLine.getOptions()) {
            System.out.println(option.getArgName() + " : " + option.getValue());
        }
    } catch (ParseException e) {
        e.printStackTrace();
    }
}
}
