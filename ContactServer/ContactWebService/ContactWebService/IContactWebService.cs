using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.ServiceModel.Channels;
using System.ServiceModel.Activation;

namespace ContactWebService
{
   [ServiceContract]
   public interface IContactWebService
   {
      [OperationContract]
      [WebGet(UriTemplate = "/ContactList", ResponseFormat = WebMessageFormat.Json)]
      ContactList GetContactList();

      [OperationContract]
      [WebGet(UriTemplate = "/Contact/{Id}", ResponseFormat = WebMessageFormat.Json)]
      ContactList GetContact(string Id);

      [OperationContract]
      [WebInvoke(UriTemplate = "/Contact/{Id}", Method = "PUT", RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json)]
      int SaveContact(string ID, Contact addEditContact);

      [OperationContract]
      [WebInvoke(UriTemplate = "/Contact/{Id}", Method = "DELETE", ResponseFormat = WebMessageFormat.Json)]
      int DeleteContact(string ID);
   }
}
