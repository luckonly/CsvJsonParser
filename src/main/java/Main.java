import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

}
