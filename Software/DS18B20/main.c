#include "reg52.h"			 //此文件中定义了单片机的一些特殊功能寄存器
#include "string.h"
#include"temp.h"	
#include"Usart.h"
 #include"1602.h"

unsigned int bai,shi,ge,fen,li;
void datapros(int temp) 	 
{
   	float tp;  
	if(temp< 0)	
  	{
		temp=temp-1;
		temp=~temp;
		tp=temp;
		temp=tp*0.0625*100+0.5;	
  	}
 	else
  	{			
		tp=temp;
		temp=tp*0.0625*100+0.5;	
	}
	bai=temp / 10000;
    shi=temp % 10000 / 1000;
	ge=temp % 1000 / 100;
	fen=temp % 100 / 10;
	li=temp % 10;
	 if((li>=0&&li<=9)&&(fen>=0&&fen<=9)&&(ge>=0&&ge<=9)&(shi>=1&&shi<=3)&(bai==0))
	 {
	ttldat[0]=bai; ttldat[1]=shi;ttldat[2]=ge;ttldat[3]=fen;ttldat[4]=li;
	}
}

 void xianshi()
{
	lcdcom(0xc5);
	lcddat(ttldat[0]+0x30);
	lcddat(	ttldat[1]+0x30);
	lcddat(ttldat[2]+0x30);
	lcddat('.');
	lcddat(ttldat[3]+0x30);
	lcddat(ttldat[4]+0x30);
	lcddat('C');
	
}

void main()
{
	lcdinit("temperature:");
    UsartInit();

	while(1)
	{
	datapros(Ds18b20ReadTemp()); 
	xianshi();
	SendData();
	delayms(100);
	}		
}
	