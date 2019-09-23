'use strict';

function getAuthorizationHeader () {
    return 'Bearer ' + localStorage.getItem('access_token');
}

function buildHtmlTable(selector, data) {
    $(selector).empty();

    var columns = addAllColumnHeaders(data, selector);

    for (var i = 0; i < data.length; i++) {
        var row$ = $('<tr/>');
        for (var colIndex = 0; colIndex < columns.length; colIndex++) {
            var cellValue = data[i][columns[colIndex]];
            if (cellValue == null) cellValue = "";
            row$.append($('<td/>').html(cellValue));
        }
        $(selector).append(row$);
    }
}

function addAllColumnHeaders(data, selector) {
    var columnSet = [];
    var headerTr$ = $('<tr/>');

    for (var i = 0; i < data.length; i++) {
        var rowHash = data[i];
        for (var key in rowHash) {
            if ($.inArray(key, columnSet) == -1) {
                columnSet.push(key);
                headerTr$.append($('<th/>').html(key));
            }
        }
    }
    $(selector).append(headerTr$);

    return columnSet;
}

$('#signup').on('submit', (e) => {
    e.preventDefault();

    $.ajax({
        type: 'POST',
        url : '/api/v1/user/signup',
        data : {
            'client_id' : $('#client_id').val(),
            'user_name' : $('#signup_user_name').val(),
            'password' : $('#signup_password').val()
        }
    }).then((response) => {
        $('#op_result').text('');
        localStorage.setItem('access_token', response.access_token);
        localStorage.setItem('refresh_token', response.refresh_token);

        $('#signup_user_name').val('');
        $('#signup_password').val('');
        $('#signin_user_name').val('');
        $('#signin_password').val('');
        $('#signin_form').hide();

        $('#token_refresh_form').show();
        $('#api_test_form').show();
    }).catch((response) => {
        $('#op_result').text(response.responseJSON.message);
    })
});

$('#signin').on('submit', (e) => {
    e.preventDefault();

    $.ajax({
        type: 'POST',
        url : '/api/v1/user/signin',
        data : {
            'client_id' : $('#client_id').val(),
            'user_name' : $('#signin_user_name').val(),
            'password' : $('#signin_password').val()
        }
    }).then((response) => {
        $('#op_result').text('');
        localStorage.setItem('access_token', response.access_token);
        localStorage.setItem('refresh_token', response.refresh_token);

        $('#signin_user_name').val('');
        $('#signin_password').val('');
        $('#signin_form').hide();

        $('#token_refresh_form').show();
        $('#api_test_form').show();
    }).catch((response) => {
        $('#op_result').text(response.responseJSON.message);
    });
});

$('#refresh_access_token').on('click', (e) => {
    $.ajax({
        type: 'POST',
        url : '/api/v1/token/refresh',
        contentType : 'application/json',
        headers : {
            'Authorization' : getAuthorizationHeader()
        },
        data : JSON.stringify({
            'refresh_token' : localStorage.getItem('refresh_token')
        })
    }).then((response) => {
        $('#op_result').text('');
        localStorage.setItem('access_token', response.access_token);
        localStorage.setItem('refresh_token', response.refresh_token);
    }).catch((response) => {
        $('#op_result').text(response.responseJSON.message);
    });
});

$('#load_stat').on('click', (e) => {
    $.ajax({
        type: 'POST',
        url : '/api/v1/statistics/load',
        headers : {
            'Authorization' : getAuthorizationHeader()
        }
    }).then((response) => {
        $('#op_result').text('');
        buildHtmlTable('#result_table', [{'result' : '성공'}]);
    }).catch((response) => {
        $('#op_result').text(response.responseJSON.message);
    });

});

$('#get_device').on('click', (e) => {
    $.ajax({
        type: 'GET',
        url : '/api/v1/devices',
        headers : {
            'Authorization' : getAuthorizationHeader()
        }
    }).then((response) => {
        $('#op_result').text('');
        buildHtmlTable('#result_table', response.devices);
    }).catch((response) => {
        $('#op_result').text(response.responseJSON.message);
    });
});

$('#get_first_rank_device').on('click', (e) => {
    $.ajax({
        type: 'GET',
        url : '/api/v1/statistics/devices/first_rank',
        headers : {
            'Authorization' : getAuthorizationHeader()
        }
    }).then((response) => {
        $('#op_result').text('');
        buildHtmlTable('#result_table', response.devices);
    }).catch((response) => {
        $('#op_result').text(response.responseJSON.message);
    });
});

$('#get_first_rank_device_of_year').on('click', (e) => {
    $('#year_input').show();
    $('#check1').show();

    $('#check1').on('click', (e) => {
        $.ajax({
            type: 'GET',
            url : '/api/v1/statistics/' + $('#year_input').val() + '/devices/first_rank',
            headers : {
                'Authorization' : getAuthorizationHeader()
            }
        }).then((response) => {
            $('#op_result').text('');
            buildHtmlTable('#result_table', [response.result]);
            $('#year_input').val('');
            $('#year_input').hide();
            $('#check1').hide();
        }).catch((response) => {
            $('#op_result').text(response.responseJSON.message);
        });
    });
});

$('#get_first_rank_year_of_device').on('click', (e) => {
    $('#device_id_input').show();
    $('#check2').show();

    $('#check2').on('click', (e) => {
        $.ajax({
            type: 'GET',
            url : '/api/v1/statistics/devices/' + $('#device_id_input').val() + '/first_rank_year',
            headers : {
                'Authorization' : getAuthorizationHeader()
            }
        }).then((response) => {
            $('#op_result').text('');
            buildHtmlTable('#result_table', [response.result]);
            $('#device_id_input').val('');
            $('#device_id_input').hide();
            $('#check2').hide();
        }).catch((response) => {
            $('#op_result').text(response.responseJSON.message);
        });
    });
});

$('#forecast').on('click', (e) => {
    $('#device_id_input2').show();
    $('#check3').show();

    $('#check3').on('click', (e) => {
        $.ajax({
            type: 'POST',
            url : '/api/v1/statistics/devices/forecast',
            contentType : 'application/json',
            headers : {
                'Authorization' : getAuthorizationHeader()
            },
            data : JSON.stringify({'device_id' : $('#device_id_input2').val()})
        }).then((response) => {
            $('#op_result').text('');
            buildHtmlTable('#result_table', [response.result]);
            $('#device_id_input2').val('');
            $('#device_id_input2').hide();
            $('#check3').hide();
        }).catch((response) => {
            $('#op_result').text(response.responseJSON.message);
        });
    });
});
