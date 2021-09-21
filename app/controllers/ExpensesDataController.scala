package controllers

import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject._

@Singleton
class ExpensesDataController @Inject() (cc: ControllerComponents) extends AbstractController(cc){

  def expenses = Action { Ok(costData) }

  def costData: JsValue = {
    Json.parse("""
    [
        { "id": 10, "firstName": "Angela", "lastName": "Merkel" },
        { "id": 20, "firstName": "Vladimir", "lastName": "Putin" },
        { "id": 30, "firstName": "David", "lastName": "Cameron" },
        { "id": 40, "firstName": "Barack", "lastName": "Obama" },
        { "id": 50, "firstName": "François", "lastName": "Hollande" }
    ]
  """)
  }
}
