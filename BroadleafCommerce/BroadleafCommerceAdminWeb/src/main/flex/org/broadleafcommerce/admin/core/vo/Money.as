package org.broadleafcommerce.admin.core.vo
{
	import flash.utils.IDataInput;
	import flash.utils.IDataOutput;
	import flash.utils.IExternalizable;
	
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.util.money.Money")]	
	public class Money implements IExternalizable
	{
		public var amount:Number; 
		public var currency:Object;

    public function readExternal(input:IDataInput):void {
		amount = input.readFloat();
		// currency = input.readObject();
    }

    public function writeExternal(output:IDataOutput):void {
        output.writeFloat(amount);
        // output.writeObject(currency);
    }

	}
}