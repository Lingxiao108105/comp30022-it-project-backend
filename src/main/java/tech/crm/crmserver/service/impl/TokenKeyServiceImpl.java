package tech.crm.crmserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import tech.crm.crmserver.common.constants.SecurityConstants;
import tech.crm.crmserver.dao.TokenKey;
import tech.crm.crmserver.dao.User;
import tech.crm.crmserver.dto.LoginRequest;
import tech.crm.crmserver.mapper.TokenKeyMapper;
import tech.crm.crmserver.security.JwtSigningKeyResolver;
import tech.crm.crmserver.service.TokenKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.crm.crmserver.service.UserService;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 *  serviceImpl
 * </p>
 *
 * @author Lingxiao
 * @since 2021-08-26
 */
@Service
public class TokenKeyServiceImpl extends ServiceImpl<TokenKeyMapper, TokenKey> implements TokenKeyService {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtSigningKeyResolver jwtSigningKeyResolver;

    public String createToken(User user) {

        Integer user_id = user.getId();
        String email = user.getEmail();

        //set expiration Date
        final Date createdDate = new Date();
        long expiration = SecurityConstants.EXPIRATION;
        final Date expirationDate = new Date(createdDate.getTime() + expiration * 1000);
        //create a random secret key
        String key = UUID.randomUUID().toString() + UUID.randomUUID().toString();
        SecretKey secretKey = Keys.hmacShaKeyFor(DatatypeConverter.parseBase64Binary(key));
        //store the secret key
        TokenKey tokenKey = new TokenKey();
        tokenKey.setJwtKey(key);
        tokenKey.setUserId(user_id);
        this.save(tokenKey);
        QueryWrapper<TokenKey> wrapper = new QueryWrapper<>();
        wrapper.eq("jwt_key",key);
        Integer id = this.getOne(wrapper).getId();
        //create token
        String tokenPrefix = Jwts.builder()
                .setHeaderParam("type", SecurityConstants.TOKEN_TYPE)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .claim("user_id",user_id)
                .setId(Integer.toString(id))
                .setIssuer("IT-project")
                .setIssuedAt(createdDate)
                .setSubject(email)
                .setExpiration(expirationDate)
                .compact();
        return SecurityConstants.TOKEN_PREFIX + tokenPrefix; // add "Bearer ";
    }

    public void removeToken(String token){
        String tokenValue = token.replace(SecurityConstants.TOKEN_PREFIX, "");
        Integer id = Integer.parseInt(getClaims(tokenValue).getId());
        removeById(id);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = getClaims(token);
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("user");
        Integer userId = (int)(double)claims.get("user_id");
        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(jwtSigningKeyResolver).build()
                .parseClaimsJws(token)
                .getBody();
    }

}