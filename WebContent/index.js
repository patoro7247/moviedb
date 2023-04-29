/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

let search_form = $("#search_form");

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    console.log(resultData[0]["genres"]);

    let rank = 1;
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        //concatenate genres
        let genreArray = resultData[i]["genres"];
        let genreString = "";

        for(let i = 0; i < genreArray.length; i++){

            let genreItem = genreArray[i]['genreName'];
            genreString = genreString + genreItem + ", ";
        }
        if(genreString.length > 1) genreString = genreString.substring(0, genreString.length-2);

        console.log(genreString);


        //concatenate stars
        let starArray = resultData[i]["stars"];
        let starString = "";

        for(let i = 0; i < starArray.length; i++){

            let starItem ='<a href="single-star.html?id=' + starArray[i]['id'] + '">'
                + starArray[i]["name"] +     // display star_name for the link text
                '</a>';
            starString = starString + starItem + ", ";
        }
        //get rid of comma
        if(starString.length > 1) starString = starString.substring(0, starString.length-2);

        console.log(starString);


        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>"+rank+"</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['id'] + '">'
            + resultData[i]["title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>" + genreString + "</th>";
        rowHTML += "<th>" + starString + "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
        rank++;
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/results", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});


function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle search response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If search succeeds, redirect with to results.html?parameters
    if (resultDataJson["status"] === "success") {
        window.location.replace("index.html");


    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}


function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/results/search", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

search_form.submit(submitSearchForm);