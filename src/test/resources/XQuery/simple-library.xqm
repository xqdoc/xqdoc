xquery version "3.1";

module namespace s="http://example.com/s";

(:~
 : Say Hello world 
 :
 : @author  Samuel Smiley 
 : @version 1.0 
 :) 

declare function s:hello() as xs:string {
    "hello world"
};