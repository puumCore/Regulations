package com.r_n_m.kws.Regulations._interface;

import com.r_n_m.kws.Regulations._entities.OneTimePassword;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface OneTimePasswordOps {

    default Supplier<Integer> generate_OTP_code() {
        return () -> {
            var random = new Random();
            var oneTimePassword = IntStream.range(0, 6)
                    .map(i -> random.nextInt(10))
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining());
            return Integer.parseInt(oneTimePassword.trim());
        };
    }

    OneTimePassword create_OTP(UUID accountId);

    /**
     * This obtains the token with the provided parameters including checking if it's within the 10seconds allowance to use the token.
     *
     * @param code      User provided OTP
     * @param accountId User account
     * @return Valid OTP otherwise null
     */
    OneTimePassword get_OTP(Integer code, UUID accountId);

    Boolean deactivate_used_OTP(OneTimePassword otp);

    Integer get_deactivated_OTPs();

}
