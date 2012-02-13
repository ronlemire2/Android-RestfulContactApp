using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ContactBusinessLogic
{
   /// <summary>
   /// Base class for all Business Logic manager objects. Persists error information.
   /// </summary>
   public class ServiceBase : IServiceBase
   {//where T : EntityObject, new() {

      //public static T GetModel() {
      //   return new T();
      //}

      protected IList<string> _errors = new List<String>();
      /// <summary>
      /// Gets a list of persisted errors for this manager.
      /// </summary>
      /// <value>The errors.</value>
      public IList<string> Errors
      { // TODO: Change to IEnumerable
         get
         {
            return _errors;
         }
      }

      /// <summary>
      /// Gets a value indicating whether this instance has errors.
      /// </summary>
      /// <value>
      /// 	<c>true</c> if this instance has errors; otherwise, <c>false</c>.
      /// </value>
      public bool HasErrors
      {
         get
         {
            return ((this.Errors != null) && (this.Errors.Count > 0));
         }
      }

      /// <summary>
      /// Gets the list of errors as a formatted sentence.
      /// </summary>
      /// <returns></returns>
      public string GetErrors()
      {
         return GetErrors(false);
      }
      /// <summary>
      /// Gets the list of errors as an HTML list with <li></li> tags.
      /// </summary>
      /// <param name="includeHtmlListTags">if set to <c>true</c> includes HTML list tags.</param>
      /// <returns></returns>
      public string GetErrors(bool includeHtmlListTags)
      {
         string result = null;

         if (this.HasErrors)
         {
            var sb = new StringBuilder();
            for (int i = 0; i < Errors.Count; i++)
            {
               if (includeHtmlListTags)
                  sb.Append("<li>");
               sb.Append(Errors[i]);

               if (includeHtmlListTags)
                  sb.Append("</li>");
               else
                  sb.Append(" ");

               sb.Append(Environment.NewLine);
            }
            result = sb.ToString();
         }

         return result;
      }
   }
}
