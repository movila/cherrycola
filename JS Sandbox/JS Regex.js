/**====================================================================
 *
 * Note on use of RegExp
 *
 * @author Shi Bai
 */
/*=====================================================================

6 JS methods use RegExp:
	1. RegExp.exec(string)
	2. RegExp.test(string)
	3. String.match(regexp)
	4. String.find(regexp)
	5. String.replace(regexp, string)
	6. String.split(regexp)

Relation between OR|, Capture(), Non-Capture(?:), Follow(?=) and Not-Follow(?!)
	/a|bc/				-->		a or bc
	/(a|b)c/			-->		ac or bc (remembered -> exec().length === 2)
	/(?:a|b)c/			-->		ac or bc (not remembered -> exec().length === 1)
	/(?:a|b)(?=c)/		-->		a or b (but ac or bc must be present in string)
	/(?:a|b)(?!c)/		-->		a or b (but bc or ac must be present in string)

OR| serve automatically as expression seperator	=> package sampling
	/^c|a|t[ch]$/		==>		match 3 expression conditions
						-->		1. 'c' for catch; 
						-->		2. 'a' for batch; 
						-->		3. null for bitch; 'th' for bith; 'tc' for bitc

Capture() or Non-Capture(?:) is a must, which serves as grouping => package wrapping
	/a|bc/			VS		/(a|b)c/
	/^a|b|c$/		VS*		/^(a|b|c)$/		==>		/^[abc]$/ is better
	/^aa|bb|cc$/	VS		/^(aa|bb|cc)$/
	/ab*c/			VS		/(ab)*c/

----------------------------------*/