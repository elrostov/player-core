package spring.app.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import spring.app.model.User;
import spring.app.service.abstraction.UserService;
import spring.app.service.EmailSender;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {	//сервис, отвечающий за получение аутентификации пользователя

	private final UserService userService;

	@Autowired
	public UserDetailsServiceImpl(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public EmailSender emailSender;

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		User user = userService.getUserByLogin(login);

		if (user == null) {
			throw new UsernameNotFoundException("Username " + login + " not found");
		}
		return user;
	}

	public void SendMail(User user){
		if(!StringUtils.isEmpty(user.getEmail())){
			String message = String.format(
					"Hello %s! \n" +
							"Body of message",
					user.getEmail()
			);
			emailSender.send(user.getEmail(), "Test", message);
		}
	}


}
