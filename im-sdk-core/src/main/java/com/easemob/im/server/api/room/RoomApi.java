package com.easemob.im.server.api.room;

import com.easemob.im.server.api.Context;
import com.easemob.im.server.api.room.admin.demote.DemoteRoomAdmin;
import com.easemob.im.server.api.room.admin.promote.PromoteRoomAdmin;
import com.easemob.im.server.api.room.admin.list.ListRoomAdmins;
import com.easemob.im.server.api.room.create.CreateRoom;
import com.easemob.im.server.api.room.detail.GetRoomDetail;
import com.easemob.im.server.api.room.list.ListRooms;
import com.easemob.im.server.api.room.list.ListRoomsResponse;
import com.easemob.im.server.api.room.member.add.AddRoomMember;
import com.easemob.im.server.api.room.member.remove.RemoveRoomMember;
import com.easemob.im.server.api.room.member.list.ListRoomMembersResponse;
import com.easemob.im.server.api.room.member.list.ListRoomMembers;
import com.easemob.im.server.api.room.superadmin.demote.DemoteRoomSuperAdmin;
import com.easemob.im.server.api.room.superadmin.list.ListRoomSuperAdmins;
import com.easemob.im.server.api.room.superadmin.promote.PromoteRoomSuperAdmin;
import com.easemob.im.server.api.room.update.UpdateRoom;
import com.easemob.im.server.api.room.update.UpdateRoomRequest;
import com.easemob.im.server.model.EMRoom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RoomApi {
    private static final List<String> EMPTY_MEMBER_LIST = new ArrayList<>();

    private static final int DEFAULT_MAX_MEMBERS = 200;

    private Context context;

    public RoomApi(Context context) {
        this.context = context;
    }

    /**
     * Create a room.
     *
     * @param name the room's name
     * @param description the room's description
     * @param owner the owner's username
     * @return A {@code Mono} which emits {@code EMRoom}.
     */
    public Mono<String> createRoom(String name, String description, String owner) {
        return CreateRoom.createRoom(this.context, name, description, owner, EMPTY_MEMBER_LIST, DEFAULT_MAX_MEMBERS);
    }

    /**
     * Create a room.
     *
     * @param name the room's name
     * @param description the room's description
     * @param owner the owner's username
     * @param members the rooms members
     * @param maxMembers max members count
     * @return A {@code Mono} which emits {@code EMRoom}.
     */
    public Mono<String> createRoom(String name, String description, String owner, List<String> members, int maxMembers) {
        return CreateRoom.createRoom(this.context, name, description, owner, members, maxMembers);
    }

    /**
     * Get the room's detail by id.
     *
     * @param id the room's id
     * @return A {@code Mono} which emits {@code EMRoomDetail}.
     */
    public Mono<EMRoom> getRoom(String id) {
        return GetRoomDetail.byId(this.context, id);
    }


    /**
     * Update the room.
     *
     * Currently, you can update:
     * - name
     * - description
     * - maxMembers
     * More fields will be added.
     *
     * To update room's name, you can:
     * <pre>{@code
     *  EMService service;
     *  service.updateRoom(roomId, request -> request.withName("some cool name")).block(timeout);
     * }</pre>
     *
     * @param id the room's id
     * @param customizer the update request customizer
     * @return A {@code Mono} which complete upon success.
     */
    public Mono<Void> updateRoom(String id, Consumer<UpdateRoomRequest> customizer) {
        return UpdateRoom.byId(this.context, id, customizer);
    }

    /**
     * List all rooms.
     *
     * @return A {@code Flux} which emits each room's id.
     */
    public Flux<String> listRoomsAll() {
        return ListRooms.all(this.context, 10);
    }

    /**
     * List rooms iteratively.
     *
     * @param limit how many rooms to return
     * @param cursor where to continue, returned in previous response.
     *               For the first call, pass {@code null}.
     * @return A {@code Mono} which emits {@code ListRoomsResponse} upon success.
     */
    public Mono<ListRoomsResponse> listRooms(int limit, String cursor) {
        return ListRooms.next(this.context, limit, cursor);
    }

    /**
     * List rooms user joined.
     *
     * @param username the user's username
     * @return A {@code Flux} of each room's id.
     */
    public Flux<String> listRoomsUserJoined(String username) {
        return ListRooms.userJoined(this.context, username);
    }

    /**
     * List room members iteratively.
     *
     * @param roomId the room's id
     * @return A {@code Flux} of member's username.
     */
    public Flux<String> listRoomMembersAll(String roomId) {
        return ListRoomMembers.all(this.context, roomId, 10);
    }

    /**
     * List room members.
     *
     * @param roomId the room's id
     * @param limit how many members to return
     * @param cursor where to start, returned in previous call.
     *               For the first call, pass {@code null}.
     * @return A {@code Mono} of {@code ListRoomMembersResponse}.
     */
    public Mono<ListRoomMembersResponse> listRoomMembers(String roomId, int limit, String cursor) {
        return ListRoomMembers.next(this.context, roomId, limit, cursor);
    }

    /**
     * Add a member to the room.
     *
     * @param roomId the room's id
     * @param username the user's username
     * @return A {@code Mono} which completes upon success.
     */
    public Mono<Void> addRoomMember(String roomId, String username) {
        return AddRoomMember.single(this.context, roomId, username);
    }

    /**
     * Remove a member from the room.
     *
     * @param roomId the room's id
     * @param username the user's username
     * @return A {@code Mono} which completes upon success.
     */
    public Mono<Void> removeRoomMember(String roomId, String username) {
        return RemoveRoomMember.single(this.context, roomId, username);
    }

    /**
     * List admins of the room.
     *
     * @param roomId the room's id
     * @return A {@code Flux} of admin username.
     */
    public Flux<String> listRoomAdminsAll(String roomId) {
        return ListRoomAdmins.all(this.context, roomId);
    }

    /**
     * Promote a room member to admin.
     *
     * @param roomId the room's id
     * @param username the member's username
     * @return A {@code Mono} which completes upon success.
     */
    public Mono<Void> promoteRoomAdmin(String roomId, String username) {
        return PromoteRoomAdmin.single(this.context, roomId, username);
    }

    /**
     * Demote a room admin to member.
     *
     * @param roomId the room's id
     * @param username the admin's username
     * @return A {@code Mono} which completes upon success.
     */
    public Mono<Void> demoteRoomAdmin(String roomId, String username) {
        return DemoteRoomAdmin.single(this.context, roomId, username);
    }
    /**
     * List Room Super Admins
     *
     * @param pagesize where to start
     * @param pagenum how many super admins to return
     * @return A {@code Flux} of super admin's username
     */
    public Flux<String> listRoomSuperAdminsAll(int pagesize, int pagenum){
        return ListRoomSuperAdmins.all(this.context, pagesize, pagenum);
    }

    /**
     * Promote room super admin to member
     *
     * @param username the member's username
     * @return A {code Mono} which completes upon success.
     */
    public Mono<Void> promoteRoomSuperAdmin(String username){
        return PromoteRoomSuperAdmin.single(this.context, username);
    }

    /**
     * Demote room super admin to member
     *
     * @param username the super admin's username
     * @return A {@code Mono} which completes upon success.
     */
    public Mono<Void> demoteRoomSuperAdmin(String username) {
        return DemoteRoomSuperAdmin.singnle(this.context, username);
    }


}
