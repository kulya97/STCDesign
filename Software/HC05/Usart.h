unsigned char ttldat[3];
 void UsartInit()
{
	SCON=0X50;			//����Ϊ������ʽ1
	TMOD=0X21;			//���ü�����������ʽ2
	PCON=0X80;			//�����ʼӱ�

	TH1=0XF3;				//��������ʼֵ���ã�ע�Ⲩ������4800��
	TL1=0XF3;

	ES=1;						//�򿪽����ж�
	EA=1;						//�����ж�
	TR1=1;					//�򿪼�����
}


 void SendByte(unsigned char dat)
 {
   TI=0;	
   SBUF=dat+0x30;
   while(!TI);			 //�ȴ������������
   TI=0;
 }
  void SendChar(unsigned char dat)
 {
	TI=0;	
	SBUF=dat;
	while(!TI);			 //�ȴ������������
	TI=0;
 }
 void SendString(unsigned char dat[])
 {		
   unsigned int i;
	 unsigned int len;
	 len=strlen(dat);
   for(i=0;i<len;i++)
   {	   
   SendChar(dat[i]);
   }
}
void SendData()
{	
    unsigned int i;
    unsigned char a[]="����:";	
	   
	  // SendString(a);
	  SendChar('D');
	   for(i=0;i<3;i++)
	   {   if(i==1)
	   		 SendChar('.');
	     SendByte(ttldat[i]);
    	}
		// SendChar('M');
	    SendChar('\r');
	    SendChar('\n');
	
 }		

			