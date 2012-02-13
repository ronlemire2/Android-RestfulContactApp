using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Xml.Linq;
using System.ServiceModel.Activation;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Diagnostics;

namespace WebHostingIIS
{
    public class EventHandlingServiceHostFactory : WebServiceHostFactory
    {
        public override ServiceHostBase CreateServiceHost(string constructorString, 
            Uri[] baseAddresses)
        {
            //note that the base class returns ServiceHostBase, but its actually
            //WebServiceHost in this case
            ServiceHostBase sh = 
                base.CreateServiceHost(constructorString, baseAddresses);
            //we can cast to WebServiceHost if we want
            WebServiceHost wsh = sh as WebServiceHost;
            //subscribe to events
            wsh.Opened += new EventHandler(wsh_Opened);
            wsh.Closed += new EventHandler(wsh_Closed);
            //I could subscribe to more events if needed
            return sh;
        }

        void wsh_Closed(object sender, EventArgs e)
        {
            Debug.WriteLine("WebServiceHost closed!");
        }

        void wsh_Opened(object sender, EventArgs e)
        {
            Debug.WriteLine("WebServiceHost opened!");
        }
    }
}
