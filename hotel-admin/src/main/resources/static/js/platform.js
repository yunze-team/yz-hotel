function slideMenu(menu) {
    $('.nav-link').children('a').each(function() {
       $(this).removeClass('active');
    });
    $('#' + menu).addClass('active');
}