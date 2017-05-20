export interface Users {
    users : User[]
}

export interface User {
    id:number;
    name:string;
    email:string;
}

export interface Tasks {
    tasks : Task[]
}

export interface Task {
    id:number;
    chore:Chore;
    userId:number;
    assignedAt:number;
    completed:boolean;
}

export interface Chore {
    id:number;
    name:string;
    points:number;
    interval?:number;
}