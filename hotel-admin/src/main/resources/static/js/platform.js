function slideMenu(menu) {
    var menu_id = '#' + menu;
    $(menu_id).addClass('active');
    $(menu_id).parent('ul').each(function() {
        if ($(this).hasClass('treeview-menu')) {
            $(this).addClass('menu-open').css('display', 'block');
        }
    });
}