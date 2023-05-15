let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle checkout result");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    console.log(resultDataJson["rowsUpdated"])
    console.log(typeof(resultDataJson["rowsUpdated"]));

    let textBox = "";
    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["rowsUpdated"] === 1) {
        textBox += resultDataJson["movieId"]+" added! "
        //window.location.replace("confirmation.html");
        $("#login_error_message").text(textBox);
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
        //window.location.replace("checkout.html");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/addmovie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);

