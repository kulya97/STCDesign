unsigned char ttldat[5];
 void UsartInit()
{
	SCON=0X50;			//����Ϊ������ʽ1
	TMOD=0X21;			//���ü�����������ʽ2
	PCON=0X80;			//�����ʼӱ�
	TH1=0XF3;				//��������ʼֵ���ã�ע�Ⲩ������4800��
	TL1=0XF3;
	ET0=1;
	TR0=1;
	ES=1;						//�򿪽����ж�
	EA=1;						//�����ж�
	TR1=1;					//�򿪼�����
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
    //unsigned char a[]="�¶�:";		   
	//SendString(a);
	 SendChar('T');
	   for(i=0;i<5;i++)
	   {   if(i==3)
	   		 SendChar('.');
	     SendChar(ttldat[i]+0x30);
    	}
	  //SendChar('C');
	    SendChar('\r');
	    SendChar('\n');
	
 }		

			