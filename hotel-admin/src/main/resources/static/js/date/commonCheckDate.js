/**
 * 传入日期小于当前日期,则返回false
 * @param startDate
 * @returns {Boolean}
 */
function checkDateEarlierCurrentDate(startDate){

    var startTimes = getDateTimes(startDate);
	
    var currentTime = new Date();
    var currentDate = new Date(currentTime.getFullYear(),  currentTime.getMonth(), currentTime.getDate());
    var currentTimes = currentDate.getTime();
	
	// 传入时间小于当前时间,则为false
    if (startTimes < currentTimes) {
        return false;
    }
    
    return true;
}

/**
 * 开始日期大于结束日期,则返回false
 * @param startDate
 * @param endDate
 * @returns {Boolean}
 */
function checkStartDateLaterEndDate(startDate, endDate){

    var startTimes = getDateTimes(startDate);
    var endTimes = getDateTimes(endDate);
	
	// 开始时间大于结束时间,则为false
    if (startTimes > endTimes) {
        return false;
    }
    
    return true;
}

/**
 * 将日期转换为秒数
 * @param date
 * @returns
 */
function getDateTimes(date){
	 
    var dates = date.split("-");
    var dateTime = new Date(dates[0], dates[1]-1, dates[2]);
    var dateTimes = dateTime.getTime();
    
    return dateTimes;
}

/**
 * 校验日期格式是否正确
 * @param DateStr
 * @returns {Boolean}
 */
function isValidDate(DateStr) {
    var sDate=DateStr.replace(/(^\s+|\s+$)/g,''); //去两边空格; 
    
    // 传入空日期
    if(sDate=='') {
    	return false; 
    }
    
    //如果格式满足YYYY-MM-DD就替换为''
    //数据库中，合法日期可以是:YYYY-MM/DD(2003-3/21),数据库会自动转换为YYYY-MM-DD格式   
    var s = sDate.replace(/[\d]{4,4}[-]{1}[\d]{ 1,2 }[-]{1}[\d]{1,2}/g,'');
    
    //说明格式满足YYYY-MM-DD或YYYY-M-DD或YYYY-M-D或YYYY-MM-D
    if (s=='') {
        var t = new Date(sDate.replace("-",'/'));
        var ar = sDate.split("-");
        if(ar[0] != t.getYear() || ar[1] != t.getMonth()+1 || ar[2] != t.getDate()) {
            //alert('错误的日期格式！格式为：YYYY-MM-DD或YYYY/MM/DD。注意闰年。');   
            return false;   
        }
    } else {
        //alert('错误的日期格式！格式为：YYYY-MM-DD或YYYY/MM/DD。注意闰年。');
        return false;   
    }
    
    return true;   
}