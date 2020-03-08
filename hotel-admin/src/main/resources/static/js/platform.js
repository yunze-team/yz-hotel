function slideMenu(menu) {
    var menu_id = '#' + menu;
    $(menu_id).addClass('active');
    $(menu_id).parent('ul').each(function() {
        if ($(this).hasClass('treeview-menu')) {
            $(this).addClass('menu-open').css('display', 'block');
        }
    });
}

/**
 * 时间格式化
 * @param value
 * @returns {string}
 */
function dateFormatter(value) {
    var date = new Date(value);
    var year = date.getFullYear().toString();
    var month = (date.getMonth() + 1);
    var day = date.getDate().toString();
    var hour = date.getHours().toString();
    var minutes = date.getMinutes().toString();
    var seconds = date.getSeconds().toString();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (seconds < 10) {
        seconds = "0" + seconds;
    }
    return year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds;
}