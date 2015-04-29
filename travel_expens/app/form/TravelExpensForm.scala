package form

/**ヘッダ情報フォーム
 * applyDate申請日 yyyy/mm
 * fullName 申請者名
 * rowNnum  行数
 * **/

    
object TravelExpensForm {
  case class HeadFormData(
    applyDate:String,
    fullName:String,
    maxRowNum:Int)

/**明細情報フォーム
 * useDay          利用者
 * travelReason    旅行事由
 * tripClass       区分
 * departure       出発地
 * destination     目的地
 * forwardPiece    往/片
 * transportation  交通手段
 * transportExpens 交通費
 * accommodation   宿泊費
 * perDiem         日当
 * remarks         備考
 * **/
case class DetailFormData(
    selectRow:List[String],
    useDay:List[String],
    travelReason:List[String],
    tripClass:List[String],
    departure:List[String],
    destination:List[String],
    forwardPiece:List[String],
    transportation:List[String],
    transportExpens:List[String],
    accommodation:List[String],
    perDiem:List[String],
    remarks:List[String])
}