using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ContactBusinessLogic
{
   public interface IServiceBase
   {

      /// <summary>
      /// Gets a list of persisted errors for this manager.
      /// </summary>
      /// <value>The errors.</value>
      IList<string> Errors { get; }
      /// <summary>
      /// Gets a value indicating whether this instance has errors.
      /// </summary>
      /// <value>
      /// 	<c>true</c> if this instance has errors; otherwise, <c>false</c>.
      /// </value>
      bool HasErrors { get; }

      /// <summary>
      /// Gets the list of errors as a formatted sentence.
      /// </summary>
      /// <returns></returns>
      string GetErrors();
      /// <summary>
      /// Gets the list of errors as an HTML list with <li></li> tags.
      /// </summary>
      /// <param name="includeHtmlListTags">if set to <c>true</c> includes HTML list tags.</param>
      /// <returns></returns>
      string GetErrors(bool includeHtmlListTags);
   }
}
