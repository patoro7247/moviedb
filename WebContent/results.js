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


function next(){
    let url = window.location.href;

    console.log(url);
    let offset = getParameterByName('offset');
    let max = getParameterByName('max');

    let newOffset = parseInt(offset) + parseInt(max);

    //window.location.search = jQuery.query.set("offset", newOffset);
    window.location.replace("results.html?title=" + title+"&year="+year+"&director="+director+"&star="+star+"&offset="+newOffset+"&max="+max)

}

function prev(){

    let offset = getParameterByName('offset');
    let max = getParameterByName('max');


    let newOffset = parseInt(offset) - parseInt(max);
    console.log("Current offset: "+offset);
    console.log("Current max: "+max);
    console.log("New Offset: "+ newOffset);

    if( newOffset < -1){
        return;
    }else{
        window.location.replace("results.html?title=" + title+"&year="+year+"&director="+director+"&star="+star+"&offset="+newOffset+"&max="+max)

    }

}

function limit(lim){
    console.log(lim);
    //change max query to lim
    window.location.replace("results.html?title=" + title+"&year="+year+"&director="+director+"&star="+star+"&offset=0"+"&max="+lim);

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
    console.log("handleResults: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    console.log(resultData[0]["genres"]);

    let rank = 1;
    // Iterate through resultData, no more than 10 entries
    //old middle conditional: i < Math.min(20, resultData.length)
    for (let i = 0; i < resultData.length; i++) {

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
        //rowHTML += "<th>"+rank+"</th>";
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
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let prefix = getParameterByName('prefix');
//console.log("prefix length: "+prefix.length);

let genre = getParameterByName('genre');
//console.log("genre length: "+genre.length);


let title = getParameterByName('title');

let year = getParameterByName('year');

let director = getParameterByName('director');

let star = getParameterByName('star');
let offset = getParameterByName('offset');
let max = getParameterByName('max');

// Makes the HTTP GET request and registers on success callback function handleResult
if(prefix != null) {
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/results?prefix=" + prefix, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}
if( genre != null){
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/results?genre=" + genre, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

if(title || year || director || star != null){
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/results?title=" + title+"&year="+year+"&director="+director+"&star="+star+"&offset="+offset+"&max="+max, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}
