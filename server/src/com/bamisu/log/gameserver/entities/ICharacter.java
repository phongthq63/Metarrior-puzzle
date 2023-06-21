package com.bamisu.log.gameserver.entities;

/**
 * Create by Popeye on 10:38 AM, 2/18/2020
 */
public interface ICharacter {
    String readID();
    Object getSkill();
    int readLevel();
    int readStar();
    String readElement();
    String readKingdom();
    int readCharacterType();    //ECharacterType
    float readHP();        //Mau
    float readSTR();       //Damage vat ly
    float readINT();       //Damage phep
    float readATK();
    float readDEX();      //Chinh xac
    float readARM();     //Giap
    float readMR();       //Khang phep
    float readDEF();
    float readAGI();     //Toc do
    float readCRIT();      //Ti le chi mang
    float readCRITBONUS();        //Damage chi mang
    float readAPEN();      //Xuyen giap
    float readMPEN();      //Xuyen khang phep
    float readDPEN();
    float readTEN();        //Khang hieu ung
    float readELU();     //Ne tranh

    int getLethal();    // +% HP, ATK, DEF, DPEN
}
