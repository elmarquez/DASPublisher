<?php
// -------------------------------------------------------
// This script provides rudimentary security for a static
// HTML page that is retrieved through a mobile device 
// QR code reader application. As you can no doubt see, 
// the approach implemented here provides only minimal 
// security to deter casual users. You should implement a 
// security approach that is appropriate for your context.
// -------------------------------------------------------

// move this into a separate, external file
$ADMIN_USER = "user";       // change to something secret
$ADMIN_PASSWORD = "pass";   // change to something secret

$DEBUG = false;             // display debugging messages
$EXPIRE_TIME = 20;          // session duration in seconds (1800 = 30 minutes)

// start the session
session_start();

// for debugging
if ($DEBUG) {
    echo phpinfo();
}

// referer ... maybe I'm just a dumb ass, but I couldn't get the functions to 
// fetch this value without throwing an error

// case: user is not authenticated
if (!isset($_SESSION['AUTHENTICATED']) || !isset($_SESSION['CREATED'])) {
    authenticate($ADMIN_USER, $ADMIN_PASSWORD);
}
// case: user is authenticated but the session has expired
else if (time() - $_SESSION['CREATED'] > $EXPIRE_TIME) {
    authenticate($ADMIN_USER, $ADMIN_PASSWORD);
}
// case: user is authenticated and the session has not expired
else {
    renderArtifactPage();
}

// authenticate the user
function authenticate($user,$pass) {
    // if credentials were submitted
    if (isset($_POST['submit'])) {
        $inputuser = trim($_POST['input_user']);
        $inputpassword = trim($_POST['input_password']);
        if (strcmp($inputuser, $user) == 0 && strcmp($inputpassword, $pass) == 0) {
            $_SESSION['AUTHENTICATED'] = 1;
            $_SESSION['CREATED'] = time();
            renderArtifactPage();
        } else {
            // submitted bad username or password
            renderLoginPage("Incorrect user name or password.");
        }
    }
    // no submission from form
    else {
        renderLoginPage("Please enter your user name and password.");
    }
}

// Display login form
function renderLoginPage($error) {
    $referer = "http://" . $_SERVER['SERVER_NAME'] . $_SERVER['REQUEST_URI'];
    echo "<!DOCTYPE html>";
    echo "\n<html lang=\"en\">";
    echo "\n<head>";
    echo "\n<meta charset=\"utf-8\" />";
    echo "\n<title>Login</title>";
    echo "\n<meta name=\"viewport\" content=\"width=640,initial-scale=1.0,minimum-scale=1.0\" />";
    echo "\n<link rel='stylesheet' href='lib/screen.css' type='text/css' media='all' />";
    echo "\n</head>";
    echo "\n<body>";
    echo "\n<div id=\"box\">";
    echo "\n<div id=\"inset\">";
    if ($error) {
        echo "\n<div id=\"title\">" . $error . "</div>";
    } else {
        echo "\n<div id=\"title\">Please enter your username and password.</div>";
    }
    echo "\n<div id=\"middle\">";
    echo "\n<form id=\"login\" action=\"" . $referer . "\" method=\"post\">";
    echo "\n<fieldset>";
    echo "\n<input name=\"input_user\" type=\"text\" placeholder=\"Username\" autofocus required />";
    echo "\n<input name=\"input_password\" type=\"password\" placeholder=\"Password\" required />";
    echo "\n</fieldset>";
    echo "\n<fieldset>";
    echo "\n<input name=\"submit\" type=\"submit\" class=\"blue\" value=\"Login\" />";
    echo "\n</fieldset>";
    echo "\n</form>";
    echo "\n</div><!-- middle -->";
    echo "\n</div>";
    echo "\n</div>";
    echo "\n</body>";
    echo "\n</html>";
    exit;
}

function renderArtifactPage() {
?>

<!DOCTYPE html>
<html>
<head>
	<title>Artifact</title>
	<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" name="viewport" />
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<link type="text/css" rel="stylesheet" href="lib/photoswipe.css" />
	<link type="text/css" rel="stylesheet" href="lib/screen.css" />
	<script type="text/javascript" src="lib/lib/klass.min.js"></script>
	<script type="text/javascript" src="lib/jquery-1.6.4.min.js"></script>
	<script type="text/javascript" src="lib/code.photoswipe.jquery-3.0.4.min.js"></script>
	<script type="text/javascript">
		(function(window, $, PhotoSwipe){
			$(document).ready(function(){
				var options = {};
				$("#Gallery a").photoSwipe(options);
			});
		}(window, window.jQuery, window.Code.PhotoSwipe));
	</script>
</head>
<body>
	<div id="metadata">
		<ul id="Gallery" class="gallery">
			<li><a href="249576873.jpg" rel="external"><img src="249576873_md.jpg" alt="artifact" /></a></li>
		</ul>
		<table cellspacing="0" cellpadding="0">
			<tr><td>Year</td><td>2011</td></tr>
			<tr><td>Semester</td><td>Fall</td></tr>
			<tr><td>Course ID</td><td>ASC301</td></tr>
			<tr><td>Course Name</td><td>Design Studio II</td></tr>
			<tr><td>Studio Master</td><td>Marco Polo</td></tr>
			<tr><td>Instructor</td><td>George Kapelos</td></tr>
			<tr><td>Assignment Name</td><td>Project 1 - Liminal Space</td></tr>
			<tr><td>Assignment Duration</td><td>2 weeks</td></tr>
			<tr><td>Student Name</td><td>Rutherford, Nicola</td></tr>
			<tr><td>Submission ID</td><td>ASC301.F11-01-01</td></tr>
			<tr><td>Evaluation</td><td>High Pass</td></tr>
		</table>
	</div>
</body>
</html>

<?php
    exit;
} // renderArtifactPage()
?>
