package core.app.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import core.app.dao.abstraction.TelegramUserDao;
import core.app.model.TelegramUser;
import core.app.service.abstraction.TelegramUserService;

import java.util.Optional;

@Service
@Transactional
public class TelegramUserServiceImpl implements TelegramUserService {

    private final TelegramUserDao telegramUserDao;

    public TelegramUserServiceImpl(TelegramUserDao telegramUserDao) {
        this.telegramUserDao = telegramUserDao;
    }

    @Override
    public boolean isTelegramUserExists(Long telegramUserId) {
        return telegramUserDao.isTelegramUserExists(telegramUserId);
    }

    @Override
    public Optional<TelegramUser> getTelegramUserById(Long id) {
        return Optional.ofNullable(telegramUserDao.getById(id));
    }

    @Override
    public void addTelegramUser(TelegramUser telegramUser) {
        if (!isTelegramUserExists(telegramUser.getId())) {
            telegramUserDao.save(telegramUser);
        }
    }

    @Override
    public void deleteById(Long id) {
        telegramUserDao.deleteById(id);
    }
}
