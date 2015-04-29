        // 末尾行追加
        function addRow(){
            document.forms[0].action = "/travel_expens/input-endRow";
            document.forms[0].submit();
        }
        // 特定行追加
        function addSpecificRow(rowNam){
        	document.getElementsByName("row_num_sel")[0].value = rowNam;
        	
            document.forms[0].action = "/travel_expens/input-specificRow";
            document.forms[0].submit();
        }
        // 選択行追加
        function addSelectRow(){
        	// チェックを付けた数を取得する
            if($("input[name='select_row']:checked").length > 0){
            	document.forms[0].action = "/travel_expens/input-selectRow";
                document.forms[0].submit();
            } else {
                alert("最低1つチェックを付けてください。");
            }
        }
        
        // コピー行追加
        function addSelectCopyRow(){
        	// チェックを付けた数を取得する
            if($("input[name='select_row']:checked").length > 0){
            	document.forms[0].action = "/travel_expens/input-selectCopyRow";
                document.forms[0].submit();
            } else {
                alert("最低1つチェックを付けてください。");
            }
        }
        
        /** 左メニュー **/
        function inputTrans(){
        	//$("#apply_travel_view").attr("method", "GET");
        	document.forms[0].method = "POST";
        	document.forms[0].action = "/travel_expens/input";
            document.forms[0].submit();
        }
        
        function displayTrans(){
        	//$("#apply_travel_view").attr("method", "GET");
        	document.forms[0].method = "POST";
        	document.forms[0].action = "/travel_expens/display";
            document.forms[0].submit();
        }
        

function getNowDateym(){
	var nowDate = new Date();
	var year    = nowDate.getFullYear();
	var month   = nowDate.getMonth()+1;
	
	if (month < 10) {
		month = '0' + month;
	}
	return year+"/"+month;
}

//aタグでの送信処理
function sendDataJs(event){

	$('#apply_travel_view').attr("action","/travel_expens/input_add");
	$('#apply_travel_view').attr("method","POST");
	$('#apply_travel_view').submit();
	
    /*var full_name = $("#full_name").val();
    var apply_date= $("#apply_date").val();*/
    
    /*var use_day_counts = ["1","1","2","2"];
    $("[name='use_day']").each(function(){
    	use_day_counts.push($(this).val());
    });
    alert(use_day_counts.toString());*/
    
    
    /*event.preventDefault();
     * jsRoutes.controllers.travel_expens.TravelExpens.insertData().ajax({
        // 送信したらボタンを使用不可にし二重投稿を防ぐ
        beforeSend: function() {
            $("#travel_submit").attr('disabled', true);
        },
        // 完了したらボタン使用不可を解除する
        complete: function() {
            $("#travel_submit").attr('disabled', false);
        },
        // 投稿が成功したら新しいコメントを表示する
        success: function(datetime) {
        	alert("成功")
        },
        error: function() {
            alert("Insert Error");
        }
    })*/
}

//送信処理
$('#apply_travel_view').submit(function(event) {
	event.preventDefault();
    var full_name = $("#full_name").val();
    var apply_date= $("#apply_date").val();
    
    jsRoutes.controllers.travel_expens.TravelExpens.insertData(apply_date,full_name).ajax({
        // 送信したらボタンを使用不可にし二重投稿を防ぐ
        beforeSend: function() {
            $("#travel_submit").attr('disabled', true);
        },
        // 完了したらボタン使用不可を解除する
        complete: function() {
            $("#travel_submit").attr('disabled', false);
        },
        // 投稿が成功したら新しいコメントを表示する
        success: function(datetime) {
        	alert("成功")
        },
        error: function() {
            alert("Insert Error");
        }
    })
});