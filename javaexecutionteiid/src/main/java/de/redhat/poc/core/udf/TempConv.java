package de.redhat.poc.core.udf;

import java.math.BigDecimal;

public class TempConv 
{
   /**
   * Converts the given Celsius temperature to Fahrenheit, and returns the
   * value.
   * @param doubleCelsiusTemp 
   * @return Fahrenheit 
   */
   public static BigDecimal celsiusToFahrenheit(BigDecimal doubleCelsiusTemp)
   {
      if (doubleCelsiusTemp == null) 
      {
         return null;
      }
      return doubleCelsiusTemp.multiply(BigDecimal.valueOf(2));
   }
}