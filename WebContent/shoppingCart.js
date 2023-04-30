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

    alert("Item added!");
}

function checkout(){
    //clicking button routes user to checkout.html/347
    //347 can be any number, that's the only number we're concerned about passing
    window.location.replace("checkout.html?total=" + netTotal);
}

function increment(title){

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/shoppingcart/add?title=" + title, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleAddToCartSuccess(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

function decrement(title){
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/shoppingcart/decrease?title=" + title, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleAddToCartSuccess(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

function remove(title){

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/shoppingcart/remove?title=" + title, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleAddToCartSuccess(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });

    //alert("Item removed");
}

function handleAddToCartSuccess(resultData) {
    console.log("movie added!");
    window.location.replace("shoppingCart.html");
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
let netTotal = 0;
function handleResult(resultData) {
    console.log("handleResults: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    console.log(resultData[0]["title"]);
    console.log(resultData[0]["quantity"]);



    // Iterate through resultData, no more than 10 entries
    //old middle conditional: i < Math.min(20, resultData.length)
    for (let i = 0; i < resultData.length; i++) {
        let total = 5*parseInt(resultData[i]["quantity"]);
        netTotal += total;

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        //rowHTML += "<th>"+rank+"</th>";

        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>$" + 5.00 + "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";

        rowHTML += "<th>" + '<button type="button" onclick="decrement(\'' +resultData[i]['title'] + '\')">'+"-"+'</button>';
        rowHTML += '<button type="button" onclick="increment(\'' +resultData[i]['title'] + '\')">'+"+"+'</button>'+"</th>";

        rowHTML += "<th>" + '<button type="button" onclick="remove(\'' +resultData[i]['title'] + '\')">'+"Del"+'</button>' +"</th>";


        rowHTML += "<th>$" + total + "</th>";

        rowHTML += "</tr>";
        //<button type="button" data-target="#navbarNav" onClick="prev()" >Prev</button>
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);

    }

    let totalElement = jQuery("#total_amount");
    let totalHTML = "<h3>$"+netTotal+" </h3>";

    totalElement.append(totalHTML);


}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/shoppingcart?retrieve=true", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});