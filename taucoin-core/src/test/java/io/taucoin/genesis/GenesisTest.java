/**
Copyright 2020 taucoin developer

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
(the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
OR OTHER DEALINGS IN THE SOFTWARE.
*/
package io.taucoin.genesis;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.frostwire.jlibtorrent.Ed25519;
import com.frostwire.jlibtorrent.Pair;

import java.math.BigInteger;
import java.util.HashMap;

import io.taucoin.config.ChainConfig;
import io.taucoin.util.ByteUtil;
import io.taucoin.param.ChainParam;
public class GenesisTest {
    private static final Logger log = LoggerFactory.getLogger("genesisTest");
    private static final ChainConfig cf  = ChainConfig.NewTauChainConfig();
    private static final String minerpubkey = "a51fcbb2167a85bf3203d5083e3d3e7d69f801ac3309cdaf77561d3025d50949";
    private static final String minerprikey = "e86c28d995b11bf4760475cc920609eb67c869d8d50927597a67dcf7034ad85d5761cef82a5c6f06d0b61160580acf47815b9e42425e02fe87fa2c632cd396da";
    private static final String sig = "ee88e46bc571d98db8f0c57ea33bee489609791edf247f43950af2d8cfe5fd4665af4b1f5ec6e71b33e4e076f508a2e95a2e259a9400570f722d11411499afd614";
    private static final String generationSig = "4a9b7fa2a4f5cfba2ac1f159c26d1e93a771024e96f28ca22d667adf98947f5f";
    private static final String communityMsg = "f901789392636f6d6d756e69747920636861696e2e2e2eb857b855636137326661396331303035333134326331333032343235343132643666366163316230336562646332636263356663373637366431633331666464623665313a6339383730316336626635323633343030303830b857b855633739386663393161343033626366633664623230666662336331643966353935326566636238653039623430393733333863623739373439393361323130633a6339383730316336626635323633343030303830b857b855313864633465653933313637373066323631636131376364336262663331393330316139663039333062653361626366666262373365373361353064396662313a6339383730316336626635323633343030303830b857b855303930313339623430636466626334653830336630613532393366333030356161623531333265303838333231626166343463323736616433636634616264613a6339383730316336626635323633343030303830";
    private static final GenesisMsg msg = new GenesisMsg(ByteUtil.toByte(communityMsg));
    private static final ChainConfig ccf = ChainConfig.NewChainConfig((byte)1,"hello world",600,minerpubkey,generationSig,msg);
    private static final ChainConfig ccf1 = ChainConfig.NewChainConfig((byte)1,"beijing",600,minerpubkey,generationSig,msg);
    private static final String genesis = "f901f701880000000000e4e1c0808721d0369d0369788083168886f901449190746175636f696e20636861696e2e2e2eb897b89535386238663937396135313164633365623161343261323338383139383838383030313666663436333338666363633862343633373936393533373765643639626133393833383063336433643665306133653365393262323361353063326136633034663366383732343932636232313662653834306138663730663130303a6339383730316336626635323633343030303830b897b89564306463313565316636653366323130396430383966313165303837393034373263643961333961636565663664663435373533633362356265653963313437363439633832646561623739323134393262356435313031323433313865393433343632656564343763616330613939383235643835366165623433343962333a6339383730316336626635323633343030303830b4544155636f696e233330302333343332333933343636333036333339333033383331363136353338333633343331333736343335b840988066f393d83e938744a68b82b306cfcdd6d36ad38060b747e5e61e4d08824f991ac5176df83482aa8979553bc4d074e412797d04b6867efc147895efc68e28a04294f0c9081ae86417d547774fc4c61c29468ea6298a1165644d323c45fb2e6d";
    private static final String cgenesis = "f9024d0188000000005ef56ac9808721d0369d03697880a04a9b7fa2a4f5cfba2ac1f159c26d1e93a771024e96f28ca22d667adf98947f5ff901789392636f6d6d756e69747920636861696e2e2e2eb857b855636137326661396331303035333134326331333032343235343132643666366163316230336562646332636263356663373637366431633331666464623665313a6339383730316336626635323633343030303830b857b855633739386663393161343033626366633664623230666662336331643966353935326566636238653039623430393733333863623739373439393361323130633a6339383730316336626635323633343030303830b857b855313864633465653933313637373066323631636131376364336262663331393330316139663039333062653361626366666262373365373361353064396662313a6339383730316336626635323633343030303830b857b855303930313339623430636466626334653830336630613532393366333030356161623531333265303838333231626166343463323736616433636634616264613a6339383730316336626635323633343030303830b83868656c6c6f20776f726c64233630302336313335333136363633363236323332333133363337363133383335363236363333333233303333b840b68160ae5d2769b306f7b908d68e573784d95aba520fc9cdd476ce39b49197d94e94c42139cce31c8beaf2075b57b8980eac589f3af1e860d1bc4c4759e39300a0a51fcbb2167a85bf3203d5083e3d3e7d69f801ac3309cdaf77561d3025d50949";
    @Test
    public void createTauGenesis(){
        Genesis genesis = new Genesis(cf);
        log.debug("is genesis block validate??: "+genesis.isGenesisParamValidate());
        log.debug(ByteUtil.toHexString(genesis.getEncoded()));
    }

    @Test
    public void createCommunityGenesis(){
        Genesis genesis = new Genesis(ccf);

        log.debug("is genesis block validate?: "+genesis.isGenesisParamValidate());
        log.debug("signature: "+genesis.signGenesisblock(ByteUtil.toByte(minerprikey)));
        String str = ByteUtil.toHexString(genesis.getEncoded());
        log.debug(str);
        log.debug("is genesis block validate??: "+genesis.isGenesisParamValidate());
        log.debug("size is: "+str.length()/2 +" bytes");
        log.debug("is signed genesis: "+genesis.verifyGenesisSig());
        log.debug("genesis hash is: "+ByteUtil.toHexString(genesis.getGenesisHash()));

        Genesis genesis1 = new Genesis(ccf1);
        String str1 = ByteUtil.toHexString(genesis1.getEncoded());
        log.debug(str1);
        log.debug("size is: "+str1.length()/2 +" bytes");
    }

    @Test
    public void decodeTauGenesis(){
        Genesis genesis = new Genesis(ByteUtil.toByte(this.genesis));
        log.debug("chainid: "+genesis.getChainid());
    }

    @Test
    public void decodeCommunityGenesis(){
        Genesis genesis = new Genesis(ByteUtil.toByte(this.cgenesis));
        log.debug("chainid: "+genesis.getChainid());
    }

}
