import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.HashMap;

public class castsParser {

    class CastPair{
        private String actorName;
        private String movieId;

        public CastPair(String actorName, String movieId){
            this.actorName = actorName;
            this.movieId = movieId;
        }

        public String getActorName(){
            return actorName;
        }

        public String getMovieId(){
            return movieId;
        }

        public String toString() {

            return "Actor Name:" + getActorName() + ", " +
                    "Movie Id:" + getMovieId() + ". ";
        }
    }


    static List<CastPair> pairs = new ArrayList<>();

    HashMap<String, Integer> idCount = new HashMap<String, Integer>();

    List<String> duplicateIds = new ArrayList<>();

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
            dom = documentBuilder.parse("casts124.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void newParseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("m");
        for (int i = 0; i < nodeList.getLength(); i++) {

            // get the films element
            Element m = (Element) nodeList.item(i);


            String movieId = getTextValue(m, "f");
            String actorName = getTextValue(m, "a");

            //skip if actor is a stage animal
            if(actorName != null && actorName.equals("s a")){
                continue;
            }

            CastPair pair = new CastPair(actorName, movieId);

            System.out.println(pair.toString());
            pairs.add(pair);
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
                //System.out.println("first child is null");
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
        if(getTextValue(ele, tagName).equals(" ")){
            return 0;
        }
        if(getTextValue(ele, tagName).contains("[1]")){
            return Integer.parseInt(getTextValue(ele, tagName).substring(3));
        }
        if(getTextValue(ele, tagName).contains("~")){
            return Integer.parseInt(getTextValue(ele, tagName).substring(1));
        }

        if(getTextValue(ele, tagName).contains("x") || getTextValue(ele, tagName).contains("y") || getTextValue(ele, tagName).contains("bb")){
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

        if(getTextValue(ele, tagName).contains("n.a.") || getTextValue(ele, tagName).length() < 4){
            return 0;
        }


        return Integer.parseInt(getTextValue(ele, tagName).substring(0,4));
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {



        for (CastPair pair : pairs) {
            System.out.println("\t" + pair.toString());
        }

        System.out.println("Total parsed " + pairs.size() + " castings!");
    }

    public static void main(String[] args)  throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // create an instance
        castsParser domParserExample = new castsParser();

        // call run example
        domParserExample.runExample();

        //domParserExample.countDuplicates();



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


        sqlInsertRecord="INSERT INTO newstars_in_movies SELECT s.id,m.id from newstars as s, newmovies as m where s.name=? AND m.id=?;";
        try {
            conn.setAutoCommit(false);

            psInsertRecord=conn.prepareStatement(sqlInsertRecord);


            for(int i=0;i<pairs.size();i++)
            {

                String actorName = pairs.get(i).getActorName();
                String movieId = pairs.get(i).getMovieId();

                psInsertRecord.setString(1, actorName);
                psInsertRecord.setString(2, movieId);


                psInsertRecord.addBatch();
            }

            iNoRows=psInsertRecord.executeBatch();
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
