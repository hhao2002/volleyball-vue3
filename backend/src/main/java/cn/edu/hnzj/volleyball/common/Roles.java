package cn.edu.hnzj.volleyball.common;

public final class Roles {

    public static final String ADMIN = "ADMIN";
    public static final String SCORER = "SCORER";
    public static final String USER = "USER";

    private Roles() {
    }

    public static boolean isAdmin(String role) {
        return ADMIN.equals(role);
    }

    public static boolean isScorer(String role) {
        return SCORER.equals(role) || ADMIN.equals(role);
    }

    public static String label(String role) {
        if (ADMIN.equals(role)) {
            return "管理员";
        }
        if (SCORER.equals(role)) {
            return "计分员";
        }
        return "普通用户";
    }
}
