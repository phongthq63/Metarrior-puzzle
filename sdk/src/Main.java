import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.auth.SocialNetworkUtils;

public class Main {

    public static void main(String[] args){
        String address = "0x79addb6d8cabcb61568f5bed4a5457f74300b07d";
        System.out.println(SocialNetworkUtils.getAddressWalletBlockchain(address));
    }
}
