import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        String fileName = "data.csv";
        List<Employee> listEmployee = new ArrayList<>();
        parseCSV(listEmployee, fileName);

        String jsonFileName = "data.json";
        listToJson(listEmployee, jsonFileName);


        List<Employee> listEmployee2 = new ArrayList<>();
        listEmployee2 = parseXML("data.xml");

        for (Employee employee : listEmployee2) {
            System.out.println(employee);
        }
        String jsonFileName2 = "data2.json";
        listToJson(listEmployee2, jsonFileName2);

    }

    public static void parseCSV(List<Employee> listEmployee, String fileName) throws FileNotFoundException {

        CSVParser csvParser  = new CSVParserBuilder().withSeparator(',').build();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(fileName)).withCSVParser(csvParser).build()) {

            String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
            ColumnPositionMappingStrategy<Employee> columnMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnMappingStrategy.setColumnMapping(columnMapping);
            columnMappingStrategy.setType(Employee.class);

            CsvToBean<Employee> csvToBean = new CsvToBean<>();
            csvToBean.setMappingStrategy(columnMappingStrategy);
            csvToBean.setCsvReader(csvReader);
            csvToBean.stream().forEach(x -> listEmployee.add(x));

        } catch (FileNotFoundException ex) {
            System.out.println(ex.fillInStackTrace());
        } catch (IOException ex) {
            System.out.println(ex.fillInStackTrace());
        }

    }

    public static void listToJson(List<Employee> listEmployee, String fileName) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(gson.toJson(listEmployee, listType));
        } catch (IOException ex) {
            System.out.println(ex.fillInStackTrace());
        }

    }

    public static List<Employee> parseXML(String fileName) {

        List<Employee> employeeList = new ArrayList<>();
        Document dom = getNewDocument(fileName);

        if (dom != null) {
            readNode(dom.getDocumentElement(),employeeList);
        }

        return employeeList;

    }

    private static void readNode(Node node, List<Employee> employeeList) {

        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node_ = nodeList.item(i);

            if (Node.ELEMENT_NODE == node_.getNodeType()) {

                if (node_.getNodeName() == "employee") {
                    employeeList.add(getEmployeeByNodeList(node_));
                }

            }

            readNode(node_, employeeList);
        }

    }

    private static Employee getEmployeeByNodeList(Node node) {

        NodeList nodeList = node.getChildNodes();

        String idAsString = nodeList.item(1).getTextContent();
        String firstName = nodeList.item(3).getTextContent();
        String secondName = nodeList.item(5).getTextContent();
        String country = nodeList.item(7).getTextContent();
        String age = nodeList.item(9).getTextContent();

        return new Employee(Long.parseLong(idAsString), firstName, secondName, country, Integer.parseInt(age));

    }

    private static Document getNewDocument(String fileName) {


        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(fileName));

            return document;

        } catch (ParserConfigurationException ex) {
            System.out.println(ex.fillInStackTrace());
        } catch (Exception ex) {
            System.out.println(ex.fillInStackTrace());
        }

        return null;

    }


}
