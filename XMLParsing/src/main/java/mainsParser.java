import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class mainsParser {

    List<Employee> employees = new ArrayList<>();

    static List<Movie> movies = new ArrayList<>();

    HashMap<String, Integer> idCount = new HashMap<String, Integer>();

    static List<String> duplicateIds = new ArrayList<>();

    List<String> problemIds = new ArrayList<>();

    Document dom;

    public void runExample() {

        // parse the xml file and get the dom object
        parseXmlFile();

        // get each employee element and create a Employee object
        newParseDocument();

        // iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("mains243.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("film");
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the directorfilms element
            Element element = (Element) nodeList.item(i);

            // get the Employee object
            Element directorInfo = (Element) element.getFirstChild();
            String director = getTextValue(directorInfo, "dirname");


           Element films = (Element) directorInfo.getNextSibling();
           NodeList filmList = films.getElementsByTagName("film");
           for(int j = 0; j < filmList.getLength(); j++){
               Element f = (Element) filmList.item(j);

               String id = getTextValue(f, "fid");
               String title = getTextValue(f, "t");

               int year = getIntValue(element, "year");

               Movie m = new Movie(id, title, year, director);
               movies.add(m);
           }

        }
    }


    private void newParseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("film");
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the directorfilms element
            Element f = (Element) nodeList.item(i);

            // get the Employee object
            String director = "NULL";
            if( getTextValue(f, "dirn") != null){
                director = getTextValue(f, "dirn");
            }

            String id = getTextValue(f, "fid");
            String title = getTextValue(f, "t");
            int year = getIntValue(f, "year");

            //check for duplicate id's here:
            if(idCount.containsKey(id)){
                int numCount = idCount.get(id);
                numCount++;
                idCount.put(id, numCount);

                //go to next iteration
                continue;

            } else{
                idCount.put(id, 1);
            }

            if( title == null || id == null){
                problemIds.add(id);
                continue;
            }

            Movie m = new Movie(id, title, year, director);
            //System.out.println(m.toString());
            movies.add(m);


        }
    }
    /**
     * It takes an employee Element, reads the values in, creates
     * an Employee object for return
     */
    private Employee parseEmployee(Element element) {

        // for each <employee> element get text or int values of
        // name ,id, age and name
        String name = getTextValue(element, "Name");
        int id = getIntValue(element, "Id");
        int age = getIntValue(element, "Age");
        String type = element.getAttribute("type");

        // create a new Employee with the value read from the xml nodes
        return new Employee(name, id, age, type);
    }

    /**
     * It takes an XML element and the tag name, look for the tag and get
     * the text content
     * i.e for <Employee><Name>John</Name></Employee> xml snippet if
     * the Element points to employee node and tagName is name it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            if( nodeList.item(0).getFirstChild() != null){
                textVal = nodeList.item(0).getFirstChild().getNodeValue();
            }else{
                System.out.println("first child is null");
                //iterate thru all children till we find a non-null
                for(int i = 1; i < nodeList.getLength(); i++){
                    //we already know first one is null, start index at 1
                    if( nodeList.item(i).getFirstChild() != null){
                        textVal = nodeList.item(i).getFirstChild().getNodeValue();
                        break;
                    }
                }
            }

        }
        return textVal;
    }

    private void countDuplicates(){
        for(String key : idCount.keySet()){
            if(idCount.get(key) > 1){
                duplicateIds.add(key);
            }
        }
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        if(getTextValue(ele, tagName).contains("x") || getTextValue(ele, tagName).contains("y")){
            //replace the x/y with a zero and return
            String newNum = getTextValue(ele, tagName);
            for(int i = 0; i < newNum.length(); i++){
                if( !Character.isDigit(newNum.charAt(i))){
                    newNum = newNum.substring(0, i) + '0' + newNum.substring(i + 1);
                }
            }

            int parsedDigit = Integer.parseInt(newNum);
            return parsedDigit;

        }else if(getTextValue(ele, tagName).contains(" ")){
            String newStr = getTextValue(ele,tagName).replace(" ", "");
            return Integer.parseInt(newStr);
        }
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {



        for (Movie employee : movies) {
            System.out.println("\t" + employee.toString());
        }

        System.out.println("Total parsed " + movies.size() + " movies");
    }

    public static void main(String[] args)  throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            // create an instance
            mainsParser domParserExample = new mainsParser();

            // call run example
            domParserExample.runExample();

            domParserExample.countDuplicates();
            System.out.println(duplicateIds.size() + " movie duplicates!");



            Connection conn = null;

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String jdbcURL="jdbc:mysql://localhost:3306/moviedb";

            try {
                conn = DriverManager.getConnection(jdbcURL,"mytestuser", "My6$Password");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            PreparedStatement psInsertRecord=null;
            String sqlInsertRecord=null;

            int[] iNoRows=null;


            sqlInsertRecord="insert into movies (id, title, year, director) values(?,?,?,?)";

            String sqlInsertRatingRecord="insert into ratings values (?, 0, 0)";

            try {
                conn.setAutoCommit(false);

                psInsertRecord=conn.prepareStatement(sqlInsertRecord);

                PreparedStatement insertRatingRecord = conn.prepareStatement(sqlInsertRatingRecord);



                for(int i=0;i<movies.size();i++)
                {

                    String id = movies.get(i).getId();
                    String title = movies.get(i).getTitle();
                    int year = movies.get(i).getYear();
                    String director = movies.get(i).getDirector();


                    psInsertRecord.setString(1, id);
                    psInsertRecord.setString(2, title);
                    psInsertRecord.setInt(3, year);
                    psInsertRecord.setString(4, director);

                    insertRatingRecord.setString(1, movies.get(i).getId());

                    psInsertRecord.addBatch();

                    insertRatingRecord.addBatch();
                }

                iNoRows=psInsertRecord.executeBatch();
                int[] iNo2Rows = insertRatingRecord.executeBatch();

                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if(psInsertRecord!=null) psInsertRecord.close();
                if(conn!=null) conn.close();
            } catch(Exception e) {
                e.printStackTrace();
            }



    }

}
