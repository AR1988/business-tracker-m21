import React, {useState} from "react";


export default (props) => {

    const [formData, setFormData] = useState({
        name: '',
        lName: ''
    })

    function handleSubmit(event) {
        event.preventDefault();
        console.log(formData)
    }

    function handleOnChange(event) {
        // event.preventDefault();
        setFormData({ ...formData,   [event.target.name]:event.target.value })
    }

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <label>
                    Name:
                    <input type="text" value={formData.name} onChange={handleOnChange} name="name"/>
                </label>
                <label>
                    Name:
                    <input type="text" value={formData.lName} onChange={handleOnChange} name="lName"/>
                </label>
                <input type="submit"/>
                <input type="reset"/>
            </form>
            <pre>{JSON.stringify(formData)}</pre>
        </div>
    )
}
