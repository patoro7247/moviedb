/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

function addToCart(title){
    console.log(title);
    //somehow add this movieId to the user's session
    // send request to server with
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/shoppingcart?title=" + title, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleAddToCartSuccess(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });

    //alert("Movie added!");

}
/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {


    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<h3>" + resultData[0]["title"] + "</h3>");

    console.log("handleResult: populating movie table from resultData");
    console.log(resultData[1]);
    let genreString = resultData[1]["genres"];
    /*
    let genreString = "";
    for(let i = 0; i < genreArray.length; i++){
        genreString += genreArray[i] + ", ";
    }
    if(genreString.length > 1) genreString = genreString.substring(0, genreString.length-2);
    */

    console.log(genreString);
    let starArray = resultData[2];
    console.log(starArray);

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["title"] + "</th>";
    rowHTML += "<th>" + resultData[0]["year"] + "</th>";
    rowHTML += "<th>" + resultData[0]["director"] + "</th>";
    rowHTML += "<th>" + resultData[0]["rating"] + "</th>";
    rowHTML += "<th>" + genreString + "</th>";
    rowHTML += "<th>";
    for(let i =0; i < starArray.length; i++){
        rowHTML += '<a href="single-star.html?id=' + starArray[i]['id'] + '">'
            + starArray[i]["name"] +     // display star_name for the link text
            '</a>';
        rowHTML += ", ";
    }
    rowHTML += "<th>";

    rowHTML += "<th>" + '<button type="button" onclick="addToCart(\'' +resultData[0]['title'] + '\')">'+"Add"+'</button>' +"</th>";

    rowHTML += "</tr>";
    movieTableBodyElement.append(rowHTML);
    // Concatenate the html tags with resultData jsonObject to create table rows

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});