package controllers

import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import javax.inject._

@Singleton
class ExpensesDataController @Inject() (cc: ControllerComponents) extends AbstractController(cc){

  /**
   * Create an Action to send the client side a JSON that contains all the relevant data that is stored in the file.
   */
  def expenses = Action { Ok(expenseData) }

  /**
   * This function opens the stored file on the system that contains the expenses Information
   * and returns it in a JSON format.
   */
  def expenseData: JsValue = {
    Json.parse("""
    [
        { "id": 10, "expenseDate": "12.02.2010", "expense": "Coffee" , "amount": "4.22", "paymentType": "Cash", "category": "Food" },
        { "id": 20, "expenseDate": "12.02.2015", "expense": "Bagel" , "amount": "7.99", "paymentType": "Cash", "category": "Food" },
        { "id": 30, "expenseDate": "12.02.2011", "expense": "New Car" , "amount": "40800", "paymentType": "Bank Transfer", "category": "Transport" },
        { "id": 40, "expenseDate": "12.03.2011", "expense": "New Tires" , "amount": "2500", "paymentType": "Credit Card", "category": "Transport" },
        { "id": 50, "expenseDate": "12.02.2012", "expense": "Overseas Trip" , "amount": "8000", "paymentType": "Credit Card", "category": "Travel" }
    ]
  """)
  }
}
