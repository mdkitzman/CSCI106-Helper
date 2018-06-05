<!doctype html>
<html>
	<head>
		<title>JavaJam Chapter ${chapterNum} Feedback</title>
		<meta charset="UTF-8">
		<link href="../prism.css" rel="stylesheet">
		<link href="../feedback.css" rel="stylesheet">
	</head>
	<body>
		<h1>
	  		JavaJam Chapter ${chapterNum} Feedback for ${studentName}
		</h1>
		<div class="rubricTable" style="width:500px;">
			<table> 
				<colgroup>
					<col style="width:70%">
					<col style="text-align:right;">
				</colgroup>
				<tbody>
					<tr>
						<td>Criteria</td><td>Points</td>
					</tr>
					<tr>
						<td>All steps have been completed</td>
						<td>0/8</td>
					</tr>
					<tr>
						<td>Each page looks like the pictures in the book</td>
						<td>0/6</td>
					</tr>
					<tr>
						<td>Each html page <a href="http://validator.w3.org/" target="_blank">
							passes validation</a> with <em>no errors</em> (warnings are ok)
						</td>
						<td>${validationPoints}/6</td>
					</tr>
				</tbody>
				<tfoot>
					<tr>
						<td>Total Points</td>
						<td>0/20</td>
					</tr>
				</tfoot>
			</table>
		</div>
		<hr>
		
<#list results as result>
		<section<#if result.hasError> class="invalid"</#if>>
			<h3>Feedback for <code>${result.resourceName}</code></h3>
		</section>

</#list>
		<script type="text/javascript" src="../prism.js"></script>
	</body>
</html>  