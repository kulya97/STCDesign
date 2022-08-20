#include"temp.h"
void Delay1ms(uint y)
{
	uint x;
	for( ; y>0; y--)
	{
		for(x=110; x>0; x--);
	}
}

uchar Ds18b20Init()
{
	uchar i;
	DSPORT = 0;		
	i = 70;	
	while(i--);//��ʱ642us
	DSPORT = 1;			//Ȼ���������ߣ����DS18B20������Ӧ�Ὣ��15us~60us����������
	i = 0;
	while(DSPORT)	//�ȴ�DS18B20��������
	{
		Delay1ms(1);
		i++;
		if(i>5)
		{
			return 0;
		}
	
	}
	return 1;
}

//��18B20д��һ���ֽ�
void Ds18b20WriteByte(uchar dat)
{
	uint i, j;

	for(j=0; j<8; j++)
	{
		DSPORT = 0;	
		i++;
		DSPORT = dat & 0x01; 
		i=6;
		while(i--);
		DSPORT = 1;	
		dat >>= 1;
	}
}
//��ȡһ���ֽ�
uchar Ds18b20ReadByte()
{
	uchar byte, bi;
	uint i, j;	
	for(j=8; j>0; j--)
	{
		DSPORT = 0;
		i++;
		DSPORT = 1;
		i++;
		i++;
		bi = DSPORT;
		byte = (byte >> 1) | (bi << 7);						  
		i = 4;		//��ȡ��֮��ȴ�48us�ٽ��Ŷ�ȡ��һ����
		while(i--);
	}				
	return byte;
}
//��18b20��ʼת���¶�
void  Ds18b20ChangTemp()
{
	Ds18b20Init();
	Delay1ms(1);
	Ds18b20WriteByte(0xcc);		//����ROM��������		 
	Ds18b20WriteByte(0x44);	    //�¶�ת������   
}


//���Ͷ�ȡ�¶�����
void  Ds18b20ReadTempCom()
{	

	Ds18b20Init();
	Delay1ms(1);
	Ds18b20WriteByte(0xcc);	 //����ROM��������
	Ds18b20WriteByte(0xbe);	 //���Ͷ�ȡ�¶�����
}
//��ȡ�������¶�
int Ds18b20ReadTemp()
{
	int temp = 0;
	uchar tmh, tml;
	Ds18b20ChangTemp();			 
	Ds18b20ReadTempCom();	
	tml = Ds18b20ReadByte();
	tmh = Ds18b20ReadByte();
	temp = tmh;
	temp <<= 8;
	temp |= tml;
	return temp;
}


