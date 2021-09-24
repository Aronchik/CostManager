package models

import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

import java.io.{BufferedWriter, FileWriter}
import java.text.NumberFormat
import scala.io.Source


class Expenses {
  private val file = "Expenses.json"
  private val formatter: NumberFormat = java.text.NumberFormat.getIntegerInstance

  /**
   * This function opens the stored file on the system that contains the expenses Information
   * and returns it in a JSON format.
   */
  def expenseData: JsValue = {
    val source = Source.fromFile(file)
    val data = try source.getLines.mkString finally source.close()
    Json.parse(data)
  }

  /**
   * The function extracts the expense amounts from the JSON data file
   * @return A collection of all the amounts, each amount is a double
   */
  private def extractAmounts: collection.Seq[Double] = {
    (expenseData \\ "amount").map(_.as[String]).flatMap(_.toDoubleOption)
  }

  /**
   * The function extracts the currencies from the JSON data file
   * @return A collection of all the currencies, each currency is a string
   */
  private def extractCurrencies: collection.Seq[String] = {
    (expenseData \\ "currency").map(_.as[String])
  }

  /**
   * save the current Expenses to a Json file
   * @param dataToSave - The current overall expenses we want to save
   */
  private def saveToJson(dataToSave: JsValue): Unit = {
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(dataToSave.toString())
    writer.close()
  }

  /**
   * Add an expense to all the current expenses
   * @param expenseToAdd - The expense we want to add
   */
  private def addExpense(expenseToAdd: JsValue): Unit = {
    val newExpenses = expenseData.as[JsArray].append(expenseToAdd)
    saveToJson(newExpenses)
  }

  /**
   * The function calculates the sum of all the expenses
   * @return A map that represents the sum of all the expenses for each currency.
   */
  def sumAllExpenses: Map[String, Double] = {
    val expenseAmounts = extractAmounts
    val currencies = extractCurrencies
    val merged = expenseAmounts.zip(currencies).map { case (a, b) => (a,b) }
    merged.groupBy(_._2).map { case (k,v) => k -> (v map (_._1)).sum }
  }

  /**
   * The function calculates the average of the expenses
   * @return the average of the expenses
   */
  def averageAllExpenses: Map[String, Double] = {
    val expenseAmounts = sumAllExpenses
    val currencies = extractCurrencies
    val currenciesCount = currencies.groupBy(identity).map { case (v, l) => (v, l.size) }

    // Precision is to 2 decimal places
    currenciesCount.map { case (k, v) => (k, ((expenseAmounts.getOrElse(k, 1.0) / v) * 100).round / 100.toDouble)}
  }

  /**
   * Recieves a String processes it and adds it to the server stored JSON.
   * @param expenseToAdd - The expense we want to add
   */
  def addExpenseFromClient (expenseToAdd: String): JsValue = {
    val replacements = Map("%22".r -> "\"", "%7B".r -> "{", "%7D".r -> "}", "%20".r -> " ")
    val replaced = replacements.foldLeft(expenseToAdd) { (s, r) => r._1.replaceAllIn(s, r._2) }
    addExpense(Json.parse(replaced))
    expenseData
  }

  def sumOfExpenses: String = {
    sumAllExpenses.map(_.productIterator.mkString(": "))
      .mkString(" | ")
  }

  def averageOfExpenses: String = {
    averageAllExpenses.map(_.productIterator.mkString(": "))
      .mkString(" | ")
  }
}
