using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ContactBusinessEntities;

namespace ContactBusinessLogic
{
   public interface IContactService : IServiceBase
   {
      List<ContactView> GetContactViewList();
      ContactView GetContactView(int Id);
      int SaveContact(Contact entity);
      int DeleteContact(int Id);
   }
}
