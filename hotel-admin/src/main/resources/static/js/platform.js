function slideMenu(menu) {
    $('.sidebar-menu').children('li').each(function() {
       $(this).removeClass('active');
    });
    $('#' + menu).addClass('active');
}