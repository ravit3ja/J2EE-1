var oTable;

$( document ).ready(function() {

//    set page event handlers
    function setEventHandlers() {
        
        // hide CRUD respnse
        $('#ajax_add_book_response_success').hide();
        $('#ajax_add_book_response_error').hide();
        
        $('#ajax_update_book_response_success').hide();
        $('#ajax_update_book_response_error').hide();
        
        $('#ajax_delete_book_response_success').hide();
        $('#ajax_delete_book_response_error').hide();
        
        // update book table
        getBook();
    }
    
    setEventHandlers();
        
    /**
     * The event handler for submit button - add book.
     * It triggers a ajax POST call to api/v1/book
     * It will submit a book entry to a Serendipity database 
     */
    var $add_book_form = $('#add_book_form');
    $('#submit_add_book').click(function(e){
        
        e.preventDefault(); // cancel form submit
        
        var jsObj = $add_book_form.serializeObject();
        var ajaxObj = {};
        
        ajaxObj = {
                    type: "POST",
                    url: "http://localhost:8080/book_shop/api/v1/book/",
                    data: JSON.stringify(jsObj),
                    contentType: "application/json",
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.log("Error " + jqXHR.getAllResponseHeaders() + " " + errorThrown);
                    },
                    success: function(data) {
                        if(data[0].HTTP_CODE == 200) {
                            $('#ajax_add_book_response_success').css({ 'width': '50%', 'margin': '0 auto' }).show().html( '<strong>Well Done!</strong> ' + data[0].MSG ).delay(10000).fadeOut();
                            $('input#book_title').val('');  // clear the text field, after book is added
                        } else {
                            $('#ajax_add_book_response_error').css({ 'width': '50%', 'margin': '0 auto' }).show().html( '<strong>Oh snap!</strong> ' + data[0].MSG ).delay(10000).fadeOut();
                        }
                    },
                    complete: function(XMLHttpRequest) {
                        //console.log(XMLHttpRequest.getAllResponseHeaders());
                        getBook();
                    },
                    dataType: "json" // request json
        };
        
        $.ajax(ajaxObj);        
    });
     
     /**
     * The event handler for submit button - update book.
     * It triggers a ajax PUT call to api/v1/book
     * It will submit a book entry update to a Serendipity database 
     */
    var $update_book_form = $('#update_book_form');
    
    $(document.body).on('click', '#book_update_button', function(e) {
        var $this = $(this);
        var book_id = $this.val();
        var $tr = $this.closest('tr');
        var book_name = $tr.find('.container_book_name').text();
        
        $('input[name="book_id"]').val(book_id);
        $('input[name="book_name"]').val(book_name);
    });
    
    $('#update_book_form_submit').click(function(e) {
        
        $($update_book_form).submit(function(){
            e.preventDefault(); // cancel form submit
        }); // submit form
       
        var obj = $update_book_form.serializeObject();
        updateBook(obj);

        $('#update-book-modal').modal('hide');
    });
    
    
     /**
     * The event handler for submit button - update book.
     * It triggers a ajax PUT call to api/v1/book
     * It will submit a book entry update to a Serendipity database 
     */
    
    $(document.body).on('click', '#book_delete_button', function(e) {
        var $this = $(this);
        var book_id = $this.val();
        var obj = {book_id : book_id};
        
        // get the name for the alert box
        var $tr = $this.closest('tr');
        var book_name = $tr.find('.container_book_name').text();
        bootbox.confirm("Are you sure you want to delete " + '"' + book_name + '"'  + " book?", function(result) {
            if(result)
                deleteBook(obj);
            else 
                return;
        });
    });
    
//    Initialize the table and child rows
    $('#book-list-table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = oTable.row( tr );
 
        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Open this row
            row.child( fnFormatDetails( row.data() ) ).show();
            tr.addClass('shown');
        }
    } );
});

/**
 * Update book names from the backend using ajax call and json response.
 * 
 * @param {type} obj
 * @returns {jqXHR}
 */
function updateBook(obj) {
    
    ajaxObj = {
        
                type: "PUT",
                url: "http://localhost:8080/book_shop/api/v1/book/",
                data: JSON.stringify(obj),
                contentType: "application/json",
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.responseText);
                },                    
                success: function(data) {
                    if(data[0].HTTP_CODE == 200) {
                        $('#ajax_update_book_response_success').css({ 'width': '50%', 'margin': '0 auto' }).show().html( '<strong>Well Done!</strong> ' + data[0].MSG ).delay(10000).fadeOut();
                    } else {
                        $('#ajax_update_book_response_error').css({ 'width': '50%', 'margin': '0 auto' }).show().html( '<strong>Oh snap!</strong> ' + data[0].MSG ).delay(10000).fadeOut();
                    }
                },
                complete: function(XMLHttpRequest) {
                    // reload inventory
                    // console.log( XMLHttpRequest.getAllResponseHeaders() );
                    getBook();
                },
                dataType: "json"  // request json
    };
    
    return $.ajax(ajaxObj);
}

/**
 * Update book names from the backend using ajax call and json response.
 * 
 * @param {type} obj
 * @returns {jqXHR}
 */
function deleteBook(obj) {
    
    ajaxObj = {
                type: "DELETE",
                url: "http://localhost:8080/book_shop/api/v1/book/",
                data: JSON.stringify(obj),
                contentType: "application/json",
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.responseText);
                },                    
                success: function(data) {
                    if(data[0].HTTP_CODE == 200) {
                        $('#ajax_delete_book_response_success').css({ 'width': '50%', 'margin': '0 auto' }).show().html( '<strong>Well Done!</strong> ' + data[0].MSG ).delay(10000).fadeOut();
                    } else {
                        $('#ajax_delete_book_response_error').css({ 'width': '50%', 'margin': '0 auto' }).show().html( '<strong>Oh snap!</strong> ' + data[0].MSG ).delay(10000).fadeOut();
                    }
                },
                complete: function(XMLHttpRequest) {
                    // reload inventory
                    // console.log( XMLHttpRequest.getAllResponseHeaders() );
                    getBook();
                },
                dataType: "json"  // request json
    };
    
    return $.ajax(ajaxObj);
}

/**
 * Get book names from the backend using ajax call and json response.
 */
function getBook() {
    
    var d = new Date().getTime();
    
    ajaxObj = {
                type: "GET",
                url: "http://localhost:8080/book_shop/api/v1/book/",
                data: "ts="+d,
                contentType: "application/json",
                error: function(jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.responseText);
                },
                success: function(data) {
//                    onBuildBookTable(data.books);

//                  check for HTTP_CODE status from bac-kend
                    doGetBookData(data);
                },
                complete: function(XMLHttpRequest) {
                    //console.log(XMLHttpRequest.getAllResponseHeaders());
                },
                dataType: "json" // request json
    };
    
    return $.ajax(ajaxObj);
}

/**
 * This function gets book data from back-end.
 * 
 * @param {type} book_list
 * @returns {undefined}
 */
function doGetBookData(book_list) {
    var aaData = [];
    var book_array = [];
    for(var book in book_list) {
        book_array.push(book);
    }
    book_array.sort();
    for(var i in book_array) {
        book = book_array[i];
        
        aaData.push({
            'book_id':      book_list[book].book_id,
            'book_name':    '<div class="container_book_name" >' + book_list[book].book_name + '</div>',
            'qty':              book_list[book].book_list.length,
            'book_list':        book_list[book].book_list,
            'updatebtncol':     '<button type="button" class="btn btn-primary btn-small" data-toggle="modal" data-target="#update-book-modal" ' + 'id="book_update_button" value="'  + book_list[book].book_id + '" >Update</button>',
            'deletebtncol':     '<button class="btn btn-danger" id="book_delete_button" value="' + book_list[book].book_id + '" type="button">Delete</button>'
        });
        
        doBuildDataTable(aaData);
    }
}

/**
 * This function build the top level table
 * 
 * @param {type} aaData
 * @returns {undefined}
 */
function doBuildDataTable(aaData) {
    
    
//    check for book quantity
//    var check_books;
//    for (var i in aaData) {
//        if(aaData[i].book_list.length == 0) {
//            alert("Yes");
//            check_books = '<img src="/book_shop/assets/images/details_open.png">';
//        } else {
//            alert("No");
//            check_books = 'No';
//        }
//    }
    
//    This table loads data by Ajax. The latest data that has been loaded is 
//    shown below. This data will update automatically as any additional data is loaded
    if($.fn.dataTable) {
        oTable = $('#book-list-table').DataTable({
            'order': [[ 0, "asc" ]],
            'destroy': true, // reloads the table after update
            'data': aaData,
            'aLengthMenu': [[10, 25, 50, -1], [10, 25, 50, 'All']],
            'columns': [
                {
                    'data': null,
                    'class': 'details-control',
                    'defaultContent': ''
                },
                { 'data': 'book_name' },
                { 'data': 'qty' },
                { 'data': 'updatebtncol' },
                { 'data': 'deletebtncol' }
            ]
        });
    }
}

function fnFormatDetails( data ) {
    // `data` is the original data object for the row
    
    var retval;
    
    if( data.book_list.length === 0 )
        return '<p>No books added!</p>';

    retval = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
    for(var i = 0; i < data.book_list.length; i++) {
        retval +=   '<tr>'+
                        '<td><ul><li>' + data.book_list[i] +
                        '</li></ul></td>'+
                    '</tr>';
    }
    retval += '</table>';
    
    return retval;
}