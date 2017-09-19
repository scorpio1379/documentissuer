/**
 * Created by Scorpio on 26.07.2017.
 */
function sendForm() {

    var tuDoc = {};
    tuDoc.form = getHTML($('#formtu162')[0]);


    $.ajax({
        //mimeType: 'text/html; charset=utf-8', // ! Need set mimeType only when run from local file
        url: './sendDocument',
        type: 'post',
        data: tuDoc,
        success: function (data) {
            alert(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            jqXHR;
            textStatus;
            errorThrown;
        }
    });
}
function setEmptyHTMLPage(){
    var hidenHTML = $('#hiddenHTMLid')[0];
    var formHTML = $('#formtu162')[0];
    hidenHTML.value =  formHTML.innerHTML;
    return false;
}

/**
 * HTML content along with the values entered by the user for form elements
 * (unlike the default browser behavior)
 *
 * @author i.carter euona.com
 * @version 1.0 2012-04-14
 * @return innerHTML (or outerHTML)
 * @param e HTMLElement
 * @param t String: "outer" OR "inner" (default)
 *
 * @effect changes defaultValue
 * @tested FF11,IE10,GC
 */
function getHTML(e,t)
{
    if(typeof t=='undefined') var t='inner';
    switch(e.nodeName.toUpperCase())
    {
        case 'INPUT':
            if(e.type=='checkbox' || e.type=='radio')
            {
                e.defaultChecked=e.checked;
                break;
            }
        case 'TEXTAREA':
            e.defaultValue=e.value;
            break;
        case 'SELECT':
            var o=e.options,i=o.length;
            while(--i > -1)
                o[i].defaultSelected=o[i].selected;
            break;
        default:
            var x=e.getElementsByTagName('input'),i=x.length;
            while(--i > -1) getHTML(x[i],t);
            x=e.getElementsByTagName('textarea');i=x.length;
            while(--i > -1) getHTML(x[i],t);
            x=e.getElementsByTagName('select');i=x.length;
            while(--i > -1) getHTML(x[i],t);
    }
    return e[t+'HTML'];
}
