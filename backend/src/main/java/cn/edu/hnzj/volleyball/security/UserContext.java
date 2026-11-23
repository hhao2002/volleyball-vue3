package cn.edu.hnzj.volleyball.security;

import cn.edu.hnzj.volleyball.common.BusinessException;
import cn.edu.hnzj.volleyball.common.Roles;

public final class UserContext {

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(CurrentUser user) {
        HOLDER.set(user);
    }

    public static CurrentUser get() {
        return HOLDER.get();
    }

    public static CurrentUser require() {
        CurrentUser user = HOLDER.get();
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        return user;
    }

    public static Long id() {
        return require().getId();
    }

    public static boolean isAdmin() {
        return Roles.isAdmin(require().getRole());
    }

    public static void clear() {
        HOLDER.remove();
    }
}
