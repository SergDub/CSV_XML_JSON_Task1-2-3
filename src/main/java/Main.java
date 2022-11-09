import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        //createCSV("1,John,Smith,USA,25");
        //createCSV("2,Inav,Petrov,RU,23");
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        writeString(json);

        List<Employee> listXml = parseXML("data.xml");
        String json1 = listToJson(listXml);
        writeString(json1);

        String json2 = readString("new_data.json");
        List<Employee> listJson = jsonToList(json2);
        for (Employee employee1 : listJson) {
            System.out.println(employee1);
        }
    }

    private static String readString(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        String completedString = builder.toString();
        System.out.println(completedString);
        br.close();
        fr.close();
        return completedString;
    }

    private static List<Employee> jsonToList(String jsonText) {

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.fromJson(jsonText, listType);

    }

    public static Employee employee1 = new Employee();
    public static Employee employee2 = new Employee();
    public static List<Employee> staff1 = new ArrayList<>();

    public static List<Employee> parseXML(String filename) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));

        Node root = doc.getDocumentElement();
        System.out.println("Корневой элемент  " + root.getNodeName());
        read(root);

        return staff1;
    }

    private static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);

            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                if (element.getTagName() == "id") {
                    employee1.id = Long.parseLong(element.getTextContent());
                }
                if (element.getTagName() == "firstName") {
                    employee1.firstName = element.getTextContent();
                }
                if (element.getTagName() == "lastName") {
                    employee1.lastName = element.getTextContent();
                }
                if (element.getTagName() == "country") {
                    employee1.country = element.getTextContent();
                }
                if (element.getTagName() == "age") {
                    employee1.age = Integer.parseInt(element.getTextContent());

                    employee2 = new Employee(employee1.id, employee1.firstName, employee1.lastName, employee1.country, employee1.age);
                    staff1.add(employee2);

                }
                //System.out.println("employee2   " + employee2);

                read(node_);
            }
        }
    }

    public static void writeString(String json) {
        try (FileWriter writer = new FileWriter("data.json", true)) {
            writer.write(json);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader("data.csv"))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            staff = csv.parse();
            staff.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static void createCSV(String data) throws IOException {

        String[] employee = data.split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv", true))) {
            writer.writeNext(employee);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
