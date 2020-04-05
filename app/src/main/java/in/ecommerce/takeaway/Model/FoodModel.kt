package `in`.ecommerce.takeaway.Model

class FoodModel {
    var id:String?=null
    var name:String?=null
    var image:String?=null
    var description:String?=null
    var price:Long=0
    var addon:List<AddonModel> = ArrayList<AddonModel>()
    var size:List<SizeModel> = ArrayList<SizeModel>()
}
